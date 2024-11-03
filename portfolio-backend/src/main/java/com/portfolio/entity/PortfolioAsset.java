package com.portfolio.entity;

import com.portfolio.entity.asset.CommonAsset;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "portfolio_asset")
public class PortfolioAsset {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "portfolio_id")
    private Portfolio portfolio;

    @ManyToOne(optional = false)
    @JoinColumn(name = "asset_id")
    private CommonAsset asset;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private Group group;

    private double quantity;
    private LocalDateTime purchaseDate;
    private double priceWhenBought;
    private double priceNow;
}
