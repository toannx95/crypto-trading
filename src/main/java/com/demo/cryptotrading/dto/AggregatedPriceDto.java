package com.demo.cryptotrading.dto;

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
public class AggregatedPriceDto {
    private Long id;
    private String symbol;
    private BigDecimal bestBid;
    private BigDecimal bestAsk;
    private String bestBidSource;
    private String bestAskSource;
    private Long version;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
