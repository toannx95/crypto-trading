package com.demo.cryptotrading.repository;

import com.demo.cryptotrading.entity.WalletBalanceEntity;
import com.demo.cryptotrading.enums.Currency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WalletBalanceRepository extends JpaRepository<WalletBalanceEntity, Long> {

    List<WalletBalanceEntity> findByUserId(Long userId);

    Optional<WalletBalanceEntity> findByUserIdAndCurrency(Long userId, Currency currency);

}
