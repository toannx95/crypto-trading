package com.demo.cryptotrading.service;

import com.demo.cryptotrading.dto.WalletBalanceDto;

import java.util.List;

public interface WalletBalanceService {

    List<WalletBalanceDto> getBalances(Long userId);

}
