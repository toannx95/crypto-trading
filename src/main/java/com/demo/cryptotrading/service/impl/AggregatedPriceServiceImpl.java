package com.demo.cryptotrading.service.impl;

import com.demo.cryptotrading.dto.AggregatedPriceDto;
import com.demo.cryptotrading.entity.AggregatedPriceEntity;
import com.demo.cryptotrading.exception.NotFoundException;
import com.demo.cryptotrading.mapper.AggregatedPriceMapper;
import com.demo.cryptotrading.repository.AggregatedPriceRepository;
import com.demo.cryptotrading.service.AggregatedPriceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AggregatedPriceServiceImpl implements AggregatedPriceService {

    private final AggregatedPriceRepository aggregatedPriceRepository;

    @Override
    public List<AggregatedPriceDto> getLatestAll(String symbol, Pageable pageable) {
        return aggregatedPriceRepository.findLatestAll(symbol, pageable).stream()
                .map(AggregatedPriceMapper.INST::entityToDto)
                .toList();
    }

    @Override
    public AggregatedPriceDto getLatestBySymbol(String symbol) {
        Optional<AggregatedPriceEntity> optionalAggregatedPrice = aggregatedPriceRepository.findBySymbol(symbol.toUpperCase());

        if (optionalAggregatedPrice.isEmpty()) {
            log.error("Price not found for symbol {}", symbol);
            throw new NotFoundException(HttpStatus.NOT_FOUND, "Price not found for symbol: " + symbol);
        }

        return AggregatedPriceMapper.INST.entityToDto(optionalAggregatedPrice.get());
    }
}
