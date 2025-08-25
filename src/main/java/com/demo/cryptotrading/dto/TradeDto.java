package com.demo.cryptotrading.dto;

import com.demo.cryptotrading.enums.TradeType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
public class TradeDto {
    private Long id;
    private String symbol;
    private TradeType tradeType;
    private BigDecimal price;
    private BigDecimal quantity;
    private BigDecimal total;           // price * qty
    private Long version;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
