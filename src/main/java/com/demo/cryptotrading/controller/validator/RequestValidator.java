package com.demo.cryptotrading.controller.validator;

import com.demo.cryptotrading.dto.request.TradeRequest;
import com.demo.cryptotrading.exception.BadRequestException;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import static com.demo.cryptotrading.constant.Constants.ALLOWED_SYMBOLS;
import static com.demo.cryptotrading.enums.TradeType.BUY;
import static com.demo.cryptotrading.enums.TradeType.SELL;

public class RequestValidator {

    public static void validateHistoryParams(Long userId, String symbol, String type,
                                             Integer page, Integer size, String sort, String order) {
        validateUserId(userId);

        if (symbol != null) {
            validateValidSymbol(symbol);
        }

        if (type != null) {
            validateValidTradeType(type);
        }

        Set<String> allowedSorts = Set.of("createdAt", "id");
        if (!allowedSorts.contains(sort)) {
            throw new BadRequestException(HttpStatus.BAD_REQUEST, "Invalid sort field: " + sort);
        }

        if (page < 0) {
            throw new BadRequestException(HttpStatus.BAD_REQUEST,
                    "Page index must not be less than zero!");
        } else if (size < 1) {
            throw new BadRequestException(HttpStatus.BAD_REQUEST,
                    "Page size must not be less than one!");
        } else if (!"asc".equals(order) && !"desc".equals(order)) {
            throw new BadRequestException(HttpStatus.BAD_REQUEST,
                    "Page order has value asc or desc!");
        }
    }

    public static void validateTradeRequest(TradeRequest request) {
        validateRequiredSymbol(request.getSymbol());
        validateValidTradeType(request.getTradeType());
    }

    public static void validateValidTradeType(String type) {
        if (!List.of(BUY.name(), SELL.name()).contains(type)) {
            throw new BadRequestException(HttpStatus.BAD_REQUEST,
                    "Invalid trade type: " + type + ". Allowed values are BUY or SELL");
        }
    }

    public static void validateUserId(Long userId) {
        if (Objects.nonNull(userId) && userId < 1) {
            throw new BadRequestException(HttpStatus.BAD_REQUEST,
                    "User Id must be greater than 0");
        }
    }

    public static void validateRequiredSymbol(String symbol) {
        if (symbol == null || symbol.isBlank()) {
            throw new BadRequestException(HttpStatus.BAD_REQUEST, "Symbol must not be blank");
        }

        validateValidSymbol(symbol);
    }

    public static void validateValidSymbol(String symbol) {
        if (!ALLOWED_SYMBOLS.contains(symbol.toUpperCase())) {
            throw new BadRequestException(HttpStatus.BAD_REQUEST,
                    "Invalid symbol: " + symbol + ". Allowed values are ETHUSDT or BTCUSDT");
        }
    }
}
