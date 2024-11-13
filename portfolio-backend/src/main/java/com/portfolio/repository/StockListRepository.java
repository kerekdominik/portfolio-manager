package com.portfolio.repository;

import com.portfolio.entity.asset.external.StockListItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StockListRepository extends JpaRepository<StockListItem, Long> {
    Optional<StockListItem> findBySymbol(String symbol);
}
