package com.demo.cryptotrading.controller;

import com.demo.cryptotrading.dto.WalletBalanceDto;
import com.demo.cryptotrading.exception.CryptoTradingException;
import com.demo.cryptotrading.service.WalletBalanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.demo.cryptotrading.controller.validator.RequestValidator.validateUserId;

@Slf4j
@RestController
@RequestMapping("/api/wallets")
@RequiredArgsConstructor
public class WalletController {

    private final WalletBalanceService walletBalanceService;

    @GetMapping("/{userId}")
    public ResponseEntity<List<WalletBalanceDto>> getBalances(@PathVariable(value = "userId") Long userId) {
        try {
            validateUserId(userId);

            return ResponseEntity.ok(walletBalanceService.getBalances(userId));
        } catch (Exception e) {
            log.error("Failed to get wallet balance!", e);
            throw new CryptoTradingException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to get wallet balance");
        }
    }

}
