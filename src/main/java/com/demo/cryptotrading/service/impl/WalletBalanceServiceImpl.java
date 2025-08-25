package com.demo.cryptotrading.service.impl;

import com.demo.cryptotrading.dto.WalletBalanceDto;
import com.demo.cryptotrading.entity.WalletBalanceEntity;
import com.demo.cryptotrading.exception.NotFoundException;
import com.demo.cryptotrading.mapper.WalletBalanceMapper;
import com.demo.cryptotrading.repository.UserRepository;
import com.demo.cryptotrading.repository.WalletBalanceRepository;
import com.demo.cryptotrading.service.WalletBalanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WalletBalanceServiceImpl implements WalletBalanceService {

    private final WalletBalanceRepository walletBalanceRepository;
    private final UserRepository userRepository;

    @Override
    public List<WalletBalanceDto> getBalances(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(HttpStatus.NOT_FOUND, "User not found: " + userId);
        }

        List<WalletBalanceEntity> walletBalanceEntities = walletBalanceRepository.findByUserId(userId);
        if (walletBalanceEntities == null || walletBalanceEntities.isEmpty()) {
            throw new NotFoundException(HttpStatus.NOT_FOUND, "Wallet Balance not found with userId: " + userId);
        }

        return walletBalanceEntities.stream()
                .map(WalletBalanceMapper.INST::entityToDto)
                .toList();
    }
}
