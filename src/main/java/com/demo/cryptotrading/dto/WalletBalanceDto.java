package com.demo.cryptotrading.dto;

import com.demo.cryptotrading.enums.Currency;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
public class WalletBalanceDto {

    private Long id;
    private Currency currency;
    private BigDecimal balance;

}
