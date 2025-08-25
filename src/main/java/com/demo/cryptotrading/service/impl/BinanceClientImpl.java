package com.demo.cryptotrading.service.impl;

import com.demo.cryptotrading.dto.BidAskDto;
import com.demo.cryptotrading.enums.Venue;
import com.demo.cryptotrading.service.exchange.BinanceClient;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BinanceClientImpl implements BinanceClient {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${app.aggregation.binance-url}")
    private String url;

    /**
     * Fetches bid/ask data for the given symbols
     *
     * Input: ["ETHUSDT", "BTCUSDT"]
     * Output: {
     *   "ETHUSDT" -> { bid=3500.00, ask=3501.00, venue=BINANCE },
     *   "BTCUSDT" -> { bid=100000.00, ask=100001.00, venue=BINANCE }
     *   }
     */
    @Override
    public Map<String, BidAskDto> fetch(List<String> symbols) throws Exception {
        // uppercase symbols
        Set<String> requestedSymbols = symbols.stream()
                .map(String::toUpperCase)
                .collect(Collectors.toSet());

        // call to binance
        String responseBody  = restTemplate.getForObject(url, String.class);

        // parse json response
        JsonNode jsonArray  = objectMapper.readTree(responseBody);

        Map<String, BidAskDto> result = new HashMap<>();
        if (jsonArray.isArray()) {
            for (JsonNode node : jsonArray) {
                String symbol = node.path("symbol").asText(); // BTCUSDT

                // skip if not requested symbol
                if (!requestedSymbols.contains(symbol)) {
                    continue;
                }

                // extract bid/ask price, default value is 0
                BigDecimal bid = new BigDecimal(node.path("bidPrice").asText("0"));
                BigDecimal ask = new BigDecimal(node.path("askPrice").asText("0"));

                result.put(symbol, new BidAskDto(bid, ask, Venue.BINANCE));
            }
        }

        //TimeUnit.SECONDS.sleep(5);
        log.info("Fetched data from Binance API.");
        return result;
    }

}
