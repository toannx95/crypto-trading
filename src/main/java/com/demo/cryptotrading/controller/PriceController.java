package com.demo.cryptotrading.controller;

import com.demo.cryptotrading.dto.AggregatedPriceDto;
import com.demo.cryptotrading.exception.CryptoTradingException;
import com.demo.cryptotrading.service.AggregatedPriceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.demo.cryptotrading.controller.validator.RequestValidator.validateRequiredSymbol;
import static com.demo.cryptotrading.controller.validator.RequestValidator.validateValidSymbol;

@Slf4j
@RestController
@RequestMapping("/api/prices")
@RequiredArgsConstructor
public class PriceController {

    private final AggregatedPriceService aggregatedPriceService;

    @GetMapping()
    public ResponseEntity<List<AggregatedPriceDto>> getAllPricesLatest(@RequestParam(name = "symbol", required = false) String symbol,
                                                                       @RequestParam(defaultValue = "0") Integer page,
                                                                       @RequestParam(defaultValue = "20") Integer size,
                                                                       @RequestParam(name = "sort", defaultValue = "createdAt") String sort,
                                                                       @RequestParam(name = "order", defaultValue = "asc") String order) {
        try {
            if (symbol != null) {
                validateValidSymbol(symbol);
            }

            Sort sortBy = Sort.by(Sort.Direction.ASC, sort);
            if ("desc".equals(order)) {
                sortBy = sortBy.descending();
            }

            Pageable pageable = PageRequest.of(page, size, sortBy);

            return ResponseEntity.ok(aggregatedPriceService.getLatestAll(symbol, pageable));
        } catch (CryptoTradingException e) {
            log.error("Failed to get all prices latest!", e);
            throw new CryptoTradingException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to get all prices latest");
        }
    }

    @GetMapping("/{symbol}")
    public ResponseEntity<AggregatedPriceDto> getLatestBySymbol(@PathVariable String symbol) {
        try {
            validateRequiredSymbol(symbol);

            return ResponseEntity.ok(aggregatedPriceService.getLatestBySymbol(symbol));
        } catch (CryptoTradingException e) {
            log.error("Failed to get price by symbol!", e);
            throw new CryptoTradingException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to get price by symbol");
        }
    }

}
