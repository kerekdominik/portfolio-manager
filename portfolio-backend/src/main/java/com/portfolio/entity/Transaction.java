package com.portfolio.entity;

import com.portfolio.entity.asset.CommonAsset;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "transaction")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "asset_id")
    private CommonAsset asset;

    @ManyToOne(optional = false)
    @JoinColumn(name = "portfolio_id")
    private Portfolio portfolio;

    private LocalDateTime transactionDate;

    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    private double quantity;
    private double price;
}
