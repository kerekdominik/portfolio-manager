package com.portfolio.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
 @Getter
@Setter
public class Stock {
    @Id
    private long id;
    private String symbol;
    private String name;
    private double price;
    private double change;
    private double percentChange;

    @ManyToOne
    private Portfolio portfolio;

    public Stock() {
        // TODO document why this constructor is empty
    }
}
