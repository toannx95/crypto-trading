package com.demo.cryptotrading.service.job;

import com.demo.cryptotrading.config.properties.AggregationProperties;
import com.demo.cryptotrading.dto.BidAskDto;
import com.demo.cryptotrading.entity.AggregatedPriceEntity;
import com.demo.cryptotrading.enums.Venue;
import com.demo.cryptotrading.repository.AggregatedPriceRepository;
import com.demo.cryptotrading.service.exchange.ExchangeClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class PriceAggregationJob {

    private final AggregatedPriceRepository aggregatedPriceRepository;
    private final AggregationProperties aggregationProperties;
    private final List<ExchangeClient> exchangeClients;

    private final ExecutorService executorService = Executors.newFixedThreadPool(4);

    public void pollAndStore() {
        List<String> symbols = aggregationProperties.getSymbols();
        if (symbols == null || symbols.isEmpty()) {
            return;
        }

        try {
            // fetch data prices from clients
            Map<String, List<BidAskDto>> pricesBySymbol = fetchAllPrices(symbols);

            // build aggregated entities
            List<AggregatedPriceEntity> aggregatedPriceEntityList = symbols.stream()
                    .map(symbol -> buildAggregated(symbol, pricesBySymbol.get(symbol)))
                    .filter(Objects::nonNull)
                    .toList();

            // save prices to DB
            if (!aggregatedPriceEntityList.isEmpty()) {
                aggregatedPriceRepository.saveAll(aggregatedPriceEntityList);
            }

            log.info("Aggregated prices success!");
        } catch (Exception ex) {
            log.error("Aggregation run failed.", ex);
        }
    }

    /**
     * Input:  ["BTCUSDT", "ETHUSDT"]
     * Output: {
     *   "BTCUSDT" -> [
     *       {bid=58760.00, ask=58761.00, venue=BINANCE},
     *       {bid=58759.00, ask=58761.00, venue=HUOBI}
     *   ],
     *   "ETHUSDT" -> [
     *       {bid=3167.00, ask=3168.00, venue=BINANCE},
     *       {bid=3166.00, ask=3169.00, venue=HUOBI}
     *   ]
     * }
     */
    private Map<String, List<BidAskDto>> fetchAllPrices(List<String> symbols) {
        List<CompletableFuture<Map<String, BidAskDto>>> dataPriceFutures = new ArrayList<>(exchangeClients.size());

        // fetch async
        for (ExchangeClient exchange : exchangeClients) {
            CompletableFuture<Map<String, BidAskDto>> future =
                    CompletableFuture
                            .supplyAsync(() -> getPriceFromClient(exchange, symbols), executorService) // run on our executor
                            .orTimeout(5, TimeUnit.SECONDS) // will fail if not done in 5s
                            .exceptionally(ex -> {
                                log.error("Fetch failed (timeout or error) from {}", exchange.getClass(), ex);
                                return Collections.emptyMap();
                            });
            dataPriceFutures.add(future);
        }

        // wait until all exchange are completed
        CompletableFuture<Void> allFutures = CompletableFuture.allOf(dataPriceFutures.toArray(new CompletableFuture[0]));
        allFutures.join();

        Map<String, List<BidAskDto>> bucket = new HashMap<>();

        for (CompletableFuture<Map<String, BidAskDto>> dataPriceFuture : dataPriceFutures) {
            Map<String, BidAskDto> result = dataPriceFuture.join();

            if (result == null || result.isEmpty()) {
                continue; // skip failed or empty results
            }

            // Merge the entries for this exchange into bucket
            for (Map.Entry<String, BidAskDto> entry : result.entrySet()) {
                String symbol = entry.getKey();
                BidAskDto bidAsk = entry.getValue();

                if (symbol == null || bidAsk == null) {
                    continue; // skip invalid entries
                }

                // append this bid/ask to existing list
                List<BidAskDto> list = bucket.get(symbol);
                if (list == null) {
                    list = new ArrayList<>();
                    bucket.put(symbol, list);
                }
                list.add(bidAsk);
            }
        }

        return bucket;
    }

    // Pick best bid (highest) and best ask (lowest) with venue tie‑break.
    private AggregatedPriceEntity buildAggregated(String symbol, List<BidAskDto> prices) {
        if (prices == null || prices.isEmpty()) return null;

        BidAskDto bestBidDto = null; // track whole object to know venue
        BidAskDto bestAskDto = null;

        for (BidAskDto ba : prices) {
            // best BID
            // - higher is better
            // - on tie, higher sourceRank wins
            if (ba.getBid() != null) {
                if (bestBidDto == null
                        || ba.getBid().compareTo(bestBidDto.getBid()) > 0
                        || (ba.getBid().compareTo(bestBidDto.getBid()) == 0 &&
                        sourceRank(ba.getVenue()) > sourceRank(bestBidDto.getVenue()))) {
                    bestBidDto = ba;
                }
            }

            // best ASK
            // - lower is better
            // - on tie, higher sourceRank wins
            if (ba.getAsk() != null) {
                if (bestAskDto == null
                        || ba.getAsk().compareTo(bestAskDto.getAsk()) < 0
                        || (ba.getAsk().compareTo(bestAskDto.getAsk()) == 0 &&
                        sourceRank(ba.getVenue()) > sourceRank(bestAskDto.getVenue()))) {
                    bestAskDto = ba;
                }
            }
        }

        if (bestBidDto == null || bestAskDto == null) {
            return null;
        }

        BigDecimal bestBid = bestBidDto.getBid();
        BigDecimal bestAsk = bestAskDto.getAsk();

        AggregatedPriceEntity aggregatedPriceEntity = aggregatedPriceRepository.findBySymbol(symbol)
                .orElseGet(AggregatedPriceEntity::new);
        aggregatedPriceEntity.setSymbol(symbol);
        aggregatedPriceEntity.setBestBid(bestBid);
        aggregatedPriceEntity.setBestAsk(bestAsk);
        aggregatedPriceEntity.setBestBidSource(bestBidDto.getVenue().name());
        aggregatedPriceEntity.setBestAskSource(bestAskDto.getVenue().name());
        return aggregatedPriceEntity;
    }

    // Higher is better. ex: BINANCE=2, HUOBI=1
    private int sourceRank(Venue venue) {
        return switch (venue) {
            case BINANCE -> 2;
            case HUOBI -> 1;
        };
    }

    private Map<String, BidAskDto> getPriceFromClient(ExchangeClient exchangeClient, List<String> symbols) {
        try {
            return exchangeClient.fetch(symbols);
        } catch (Exception e) {
            log.error("Fetch failed from {}", exchangeClient.getClass().getName(), e);
            return Collections.emptyMap();
        }
    }

}
