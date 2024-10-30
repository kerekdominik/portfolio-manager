package com.portfolio.entity.asset;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "\"crypto_list\"")
public class CryptoListItem {
    @Id
    private String id; // By CoinGecko API

    private String symbol;
    private String name;
}