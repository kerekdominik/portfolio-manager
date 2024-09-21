package com.portfolio.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Cryptocurrency {
    @Id
    private Long id;
    private String symbol;
    private String name;
    private double price;
    private double change;

    @ManyToOne
    private Portfolio portfolio;
}
