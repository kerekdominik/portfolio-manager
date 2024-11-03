package com.portfolio.repository;

import com.portfolio.entity.asset.external.CryptoListItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CryptoListRepository extends JpaRepository<CryptoListItem, String> {
}
