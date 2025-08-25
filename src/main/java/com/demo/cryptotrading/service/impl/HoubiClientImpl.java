package com.demo.cryptotrading.service.impl;

import com.demo.cryptotrading.dto.BidAskDto;
import com.demo.cryptotrading.enums.Venue;
import com.demo.cryptotrading.service.exchange.HoubiClient;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class HoubiClientImpl implements HoubiClient {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${app.aggregation.huobi-url}")
    private String url;

    /**
     * Fetches bid/ask data for the given symbols
     *
     * Input:  ["ethusdt", "btcusdt"]
     * Output: {
     *   "ETHUSDT" -> { bid=3500.00, ask=3501.00, venue=BINANCE },
     *   "BTCUSDT" -> { bid=100000.00, ask=100001.00, venue=BINANCE }
     *   }
     */
    @Override
    public Map<String, BidAskDto> fetch(List<String> symbols) throws Exception {
        // lowercase symbols
        Set<String> requestedSymbols = symbols.stream()
                .map(String::toLowerCase)
                .collect(Collectors.toSet());

        // call to houbi api
        String responseBody  = restTemplate.getForObject(url, String.class);

        // parse json
        JsonNode rootNode  = objectMapper.readTree(responseBody);
        JsonNode dataArray  = rootNode.path("data");

        Map<String, BidAskDto> result = new HashMap<>();
        if (dataArray.isArray()) {
            for (JsonNode node : dataArray) {
                String symbol = node.path("symbol").asText(); // btcusdt

                // skip if not requested symbol
                if (!requestedSymbols.contains(symbol)) {
                    continue;
                }

                // extract bid/ask price, default value is 0
                BigDecimal bid = new BigDecimal(node.get("bid").asText("0"));
                BigDecimal ask = new BigDecimal(node.get("ask").asText("0"));

                // add to result and upper case like BTCUSDT
                result.put(symbol.toUpperCase(), new BidAskDto(bid, ask, Venue.HUOBI));
            }
        }

        //TimeUnit.SECONDS.sleep(5);
        log.info("Fetched data from Houbi API.");
        return result;
    }

}
