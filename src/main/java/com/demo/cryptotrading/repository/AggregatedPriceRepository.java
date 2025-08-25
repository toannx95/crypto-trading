package com.demo.cryptotrading.repository;

import com.demo.cryptotrading.entity.AggregatedPriceEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AggregatedPriceRepository extends JpaRepository<AggregatedPriceEntity, Long> {

    @Query("""
           SELECT a FROM AggregatedPriceEntity a
           WHERE (:symbol IS NULL OR a.symbol = :symbol)
           """)
    Page<AggregatedPriceEntity> findLatestAll(@Param("symbol") String symbol, Pageable pageable);

    Optional<AggregatedPriceEntity> findBySymbol(String symbol);

}
