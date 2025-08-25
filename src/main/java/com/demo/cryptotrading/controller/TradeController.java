package com.demo.cryptotrading.controller;

import com.demo.cryptotrading.dto.TradeDto;
import com.demo.cryptotrading.dto.request.TradeRequest;
import com.demo.cryptotrading.enums.TradeType;
import com.demo.cryptotrading.exception.CryptoTradingException;
import com.demo.cryptotrading.service.TradeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.demo.cryptotrading.controller.validator.RequestValidator.validateHistoryParams;
import static com.demo.cryptotrading.controller.validator.RequestValidator.validateTradeRequest;
import static com.demo.cryptotrading.controller.validator.RequestValidator.validateUserId;

@Slf4j
@RestController
@RequestMapping("/api/trades")
@RequiredArgsConstructor
public class TradeController {

    private final TradeService tradingService;

    @PostMapping("/{userId}")
    public ResponseEntity<TradeDto> executeTrade(@PathVariable(value = "userId") Long userId,
                                                 @RequestBody TradeRequest request) {
        try {
            validateUserId(userId);
            validateTradeRequest(request);

            return ResponseEntity.ok(tradingService.executeTrade(userId, request));
        } catch (CryptoTradingException e) {
            log.error("Failed to execute trade!", e);
            throw new CryptoTradingException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to execute trade");
        }
    }

    @GetMapping()
    public ResponseEntity<List<TradeDto>> getHistory(@RequestParam(value = "userId") Long userId,
                                                     @RequestParam(name = "symbol", required = false) String symbol,
                                                     @RequestParam(name = "type", required = false) String type,
                                                     @RequestParam(defaultValue = "0") Integer page,
                                                     @RequestParam(defaultValue = "20") Integer size,
                                                     @RequestParam(name = "sort", defaultValue = "createdAt") String sort,
                                                     @RequestParam(name = "order", defaultValue = "asc") String order) {
        try {
            validateHistoryParams(userId, symbol, type, page, size, sort, order);

            Sort sortBy = Sort.by(Sort.Direction.ASC, sort);
            if ("desc".equals(order)) {
                sortBy = sortBy.descending();
            }

            TradeType tradeType = null;
            if (type != null) {
                tradeType = TradeType.valueOf(type);
            }
            Pageable pageable = PageRequest.of(page, size, sortBy);

            return ResponseEntity.ok(tradingService.getHistory(userId, symbol, tradeType, pageable));
        } catch (Exception e) {
            log.error("Failed to fetch trade history!", e);
            throw new CryptoTradingException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to fetch trade history");
        }
    }

}
