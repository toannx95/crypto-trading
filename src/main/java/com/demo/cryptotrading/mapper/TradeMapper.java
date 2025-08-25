package com.demo.cryptotrading.mapper;

import com.demo.cryptotrading.dto.TradeDto;
import com.demo.cryptotrading.entity.TradeEntity;
import com.demo.cryptotrading.enums.TradeType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper
public interface TradeMapper {
    TradeMapper INST = Mappers.getMapper(TradeMapper.class);

    @Mapping(source = "type", target = "tradeType", qualifiedByName = "mapTradeType")
    TradeDto entityToDto(TradeEntity tradeEntity);

    @Named("mapTradeType")
    default String mapTradeType(TradeType tradeType) {
        return tradeType != null ? tradeType.toString() : null;
    }
}
