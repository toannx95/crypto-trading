package com.demo.cryptotrading.service.exchange;

import com.demo.cryptotrading.dto.BidAskDto;

import java.util.List;
import java.util.Map;

public interface ExchangeClient {

    Map<String, BidAskDto> fetch(List<String> symbols) throws Exception;

}
