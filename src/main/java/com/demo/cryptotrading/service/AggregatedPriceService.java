package com.demo.cryptotrading.service;

import com.demo.cryptotrading.dto.AggregatedPriceDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AggregatedPriceService {

    List<AggregatedPriceDto> getLatestAll(String symbol, Pageable pageable);

    AggregatedPriceDto getLatestBySymbol(String symbol);

}
