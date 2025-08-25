package com.demo.cryptotrading.mapper;

import com.demo.cryptotrading.dto.AggregatedPriceDto;
import com.demo.cryptotrading.entity.AggregatedPriceEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AggregatedPriceMapper {
    AggregatedPriceMapper INST = Mappers.getMapper(AggregatedPriceMapper.class);

    AggregatedPriceDto entityToDto(AggregatedPriceEntity aggregatedPriceEntity);

}
