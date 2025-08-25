package com.demo.cryptotrading.mapper;

import com.demo.cryptotrading.dto.WalletBalanceDto;
import com.demo.cryptotrading.entity.WalletBalanceEntity;
import com.demo.cryptotrading.enums.Currency;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper
public interface WalletBalanceMapper {
    WalletBalanceMapper INST = Mappers.getMapper(WalletBalanceMapper.class);

    @Mapping(source = "currency", target = "currency", qualifiedByName = "mapCurrency")
    WalletBalanceDto entityToDto(WalletBalanceEntity walletBalanceEntity);

    @Named("mapCurrency")
    default String mapCurrency(Currency currency) {
        return currency != null ? currency.toString() : null;
    }

}
