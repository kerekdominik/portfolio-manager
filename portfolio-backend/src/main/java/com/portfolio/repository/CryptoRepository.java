package com.portfolio.repository;

import com.portfolio.entity.asset.Crypto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CryptoRepository extends JpaRepository<Crypto, Long> {
    List<Crypto> findAllById(Long userId);
}
