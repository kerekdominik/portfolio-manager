package com.portfolio.entity.asset;

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
public abstract class CommonAsset {
    @Id
    private long id;
    private String symbol;
    private String name;
    private double priceWhenBought;
    private double priceNow;

    @ManyToOne
    private Portfolio portfolio;

    private LocalDateTime purchaseDate;
}