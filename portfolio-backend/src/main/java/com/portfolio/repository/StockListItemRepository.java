package com.portfolio.repository;

import com.portfolio.entity.asset.external.StockListItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StockListItemRepository extends JpaRepository<StockListItem, Long> {
}
