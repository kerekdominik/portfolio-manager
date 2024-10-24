package com.portfolio.entity.asset;

import com.portfolio.entity.Group;
import com.portfolio.entity.Portfolio;
import com.portfolio.entity.enums.AssetCategory;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "common_asset")
public abstract class CommonAsset {
    @Id
    private long id;
    private String symbol;
    private String name;
    private double priceWhenBought;
    private double priceNow;

    @ManyToOne
    private Portfolio portfolio;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private Group group;

    @Enumerated(EnumType.STRING)
    private AssetCategory category;

    private LocalDateTime purchaseDate;
}