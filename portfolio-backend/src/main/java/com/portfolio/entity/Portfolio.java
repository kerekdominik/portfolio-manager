package com.portfolio.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "portfolio")
public class Portfolio {
    @Id
    private long id;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private  User user;
    @OneToMany(mappedBy = "portfolio")
    private List<Stock> stocks;

    @OneToMany(mappedBy = "portfolio")
    private List<Cryptocurrency> cryptocurrencies;

    @OneToMany(mappedBy = "portfolio")
    private List<Group> groups;

    public Portfolio(long id, User user, List<Stock> stocks, List<Cryptocurrency> cryptocurrencies, List<Group> groups) {
        this.id = id;
        this.user = user;
        this.stocks = stocks;
        this.cryptocurrencies = cryptocurrencies;
        this.groups = groups;
    }

    public Portfolio() {

    }
}
