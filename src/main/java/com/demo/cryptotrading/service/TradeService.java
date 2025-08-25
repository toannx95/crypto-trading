package com.demo.cryptotrading.service;

import com.demo.cryptotrading.dto.TradeDto;
import com.demo.cryptotrading.dto.request.TradeRequest;
import com.demo.cryptotrading.enums.TradeType;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TradeService {

    TradeDto executeTrade(Long userId, TradeRequest tradeRequest);

    List<TradeDto> getHistory(Long userId, String symbol, TradeType type, Pageable pageable);
}
