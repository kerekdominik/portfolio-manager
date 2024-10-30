package com.portfolio.entity.asset;

import com.portfolio.entity.Group;
import com.portfolio.entity.Portfolio;
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
    private double quantity;

    @ManyToOne
    private Portfolio portfolio;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private Group group;

    private LocalDateTime purchaseDate;
}