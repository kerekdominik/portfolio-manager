package com.portfolio.repository;

import com.portfolio.entity.asset.CryptoListItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CryptoListRepository extends JpaRepository<CryptoListItem, Long> {
}
