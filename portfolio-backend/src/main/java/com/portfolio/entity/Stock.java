package com.portfolio.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "stock")
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

    public Stock(long id, String symbol, String name, double price, double change, double percentChange, Portfolio portfolio) {
        this.id = id;
        this.symbol = symbol;
        this.name = name;
        this.price = price;
        this.change = change;
        this.percentChange = percentChange;
        this.portfolio = portfolio;
    }

    public Stock() {
    }
}
