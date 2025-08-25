package com.demo.cryptotrading.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "app.aggregation")
public class AggregationProperties {

    private long intervalMs = 10000;
    private String binanceUrl;
    private String huobiUrl;
    private List<String> symbols;

}
