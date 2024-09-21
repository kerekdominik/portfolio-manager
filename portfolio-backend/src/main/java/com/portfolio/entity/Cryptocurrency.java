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
@Table(name = "cryptocurrency")
public class Cryptocurrency {
    @Id
    private Long id;
    private String symbol;
    private String name;
    private double price;
    private double change;

    @ManyToOne
    private Portfolio portfolio;

    public Cryptocurrency(long id, String symbol, String name, double price, double change, Portfolio portfolio) {
        this.id = id;
        this.symbol = symbol;
        this.name = name;
        this.price = price;
        this.change = change;
        this.portfolio = portfolio;
    }

    public Cryptocurrency() {

    }
}
