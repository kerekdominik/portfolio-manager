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
}
