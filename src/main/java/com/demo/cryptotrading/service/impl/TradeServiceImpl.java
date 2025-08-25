package com.demo.cryptotrading.service.impl;

import com.demo.cryptotrading.dto.TradeDto;
import com.demo.cryptotrading.dto.request.TradeRequest;
import com.demo.cryptotrading.entity.AggregatedPriceEntity;
import com.demo.cryptotrading.entity.TradeEntity;
import com.demo.cryptotrading.entity.UserEntity;
import com.demo.cryptotrading.entity.WalletBalanceEntity;
import com.demo.cryptotrading.enums.Currency;
import com.demo.cryptotrading.enums.TradeType;
import com.demo.cryptotrading.exception.BadRequestException;
import com.demo.cryptotrading.exception.InsufficientBalanceException;
import com.demo.cryptotrading.exception.NotFoundException;
import com.demo.cryptotrading.mapper.TradeMapper;
import com.demo.cryptotrading.repository.AggregatedPriceRepository;
import com.demo.cryptotrading.repository.TradeRepository;
import com.demo.cryptotrading.repository.UserRepository;
import com.demo.cryptotrading.repository.WalletBalanceRepository;
import com.demo.cryptotrading.service.TradeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TradeServiceImpl implements TradeService {

    private final AggregatedPriceRepository aggregatedPriceRepository;
    private final WalletBalanceRepository walletBalanceRepository;
    private final TradeRepository tradeRepository;
    private final UserRepository userRepository;

    private static final int COIN_SCALE = 8;

    @Override
    @Transactional
    public TradeDto executeTrade(Long userId, TradeRequest request) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(HttpStatus.NOT_FOUND, "User not found: " + userId));

        String tradingPair = request.getSymbol().toUpperCase();
        TradeType tradeType = TradeType.valueOf(request.getTradeType());

        // validate trading symbol
        AggregatedPriceEntity aggregatedPriceEntity = aggregatedPriceRepository.findBySymbol(tradingPair)
                .orElseThrow(() -> new NotFoundException(HttpStatus.NOT_FOUND, "No aggregated price for " + tradingPair));

        // identify trading price
        // bid for SELL, ask for BUY
        BigDecimal executePrice = (tradeType == TradeType.BUY) ? aggregatedPriceEntity.getBestAsk()
                : aggregatedPriceEntity.getBestBid();
        BigDecimal quantity = request.getQuantity().setScale(COIN_SCALE, RoundingMode.HALF_UP);
        BigDecimal totalUsdt = executePrice.multiply(quantity).setScale(COIN_SCALE, RoundingMode.HALF_UP);

        Currency tradingCurrency = getTradingCurrencyFromSymbol(tradingPair);   // BTC or ETH

        // load USDT balance
        WalletBalanceEntity usdtBalance = walletBalanceRepository.findByUserIdAndCurrency(userId, Currency.USDT)
                .orElseThrow(() -> new NotFoundException(HttpStatus.NOT_FOUND,
                        "USDT wallet missing for user " + userId));

        // load trading balance, if not exist, will create new
        WalletBalanceEntity tradingBalance = walletBalanceRepository.findByUserIdAndCurrency(userId, tradingCurrency)
                .orElseGet(() -> walletBalanceRepository.save(
                        WalletBalanceEntity.builder()
                                .user(user)
                                .currency(tradingCurrency)
                                .balance(BigDecimal.ZERO)
                                .build()
                ));

        if (tradeType == TradeType.BUY) {
            // ensure enough USDT balance
            if (usdtBalance.getBalance().compareTo(totalUsdt) < 0) {
                throw new InsufficientBalanceException(HttpStatus.INTERNAL_SERVER_ERROR,
                        "Insufficient USDT. Need " + totalUsdt + ", have " + usdtBalance.getBalance());
            }
            usdtBalance.setBalance(usdtBalance.getBalance().subtract(totalUsdt));
            tradingBalance.setBalance(tradingBalance.getBalance().add(quantity));
        } else { // SELL
            // ensure enough trading currency balance
            if (tradingBalance.getBalance().compareTo(quantity) < 0) {
                throw new InsufficientBalanceException(HttpStatus.INTERNAL_SERVER_ERROR,
                        "Insufficient " + tradingCurrency + ". Need " + quantity + ", have " + tradingBalance.getBalance());
            }
            tradingBalance.setBalance(tradingBalance.getBalance().subtract(quantity));
            usdtBalance.setBalance(usdtBalance.getBalance().add(totalUsdt));
        }

        // persist balance changes to DB
        walletBalanceRepository.saveAll(List.of(usdtBalance, tradingBalance));

        // persist trade to DB
        TradeEntity tradeEntity = tradeRepository.save(TradeEntity.builder()
                .user(user)
                .symbol(tradingPair)
                .type(tradeType)
                .price(executePrice)
                .quantity(quantity)
                .total(totalUsdt)
                .build());

        log.info("Completed trade on userId: {}, pair: {}, type: {}, qty: {}, price: {}",
                userId, tradingPair, tradeType, quantity, executePrice);
        return TradeMapper.INST.entityToDto(tradeEntity);
    }

    @Override
    public List<TradeDto> getHistory(Long userId, String symbol, TradeType type, Pageable pageable) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(HttpStatus.NOT_FOUND, "User not found: " + userId);
        }

        Specification<TradeEntity> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // join with user and check user.id
            predicates.add(cb.equal(root.get("user").get("id"), userId));

            if (symbol != null) {
                predicates.add(cb.equal(root.get("symbol"), symbol.toUpperCase()));
            }
            if (type != null) {
                predicates.add(cb.equal(root.get("type"), type));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<TradeEntity> trades = tradeRepository.findAll(spec, pageable);
        return trades.stream()
                .map(TradeMapper.INST::entityToDto)
                .toList();
    }

    private Currency getTradingCurrencyFromSymbol(String symbol) {
        if (symbol.startsWith("BTC")) {
            return Currency.BTC;
        }

        if (symbol.startsWith("ETH")) {
            return Currency.ETH;
        }

        throw new BadRequestException(HttpStatus.BAD_REQUEST, "Unsupported symbol: " + symbol);
    }

}
