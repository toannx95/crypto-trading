package com.demo.cryptotrading.config.job;

import com.demo.cryptotrading.service.job.PriceAggregationJob;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JobConfig {

    private final PriceAggregationJob priceAggregationJob;

    @Scheduled(fixedDelayString = "${app.aggregation.interval-ms:10000}")
    public void aggregationJob() {
        priceAggregationJob.pollAndStore();
    }

}
