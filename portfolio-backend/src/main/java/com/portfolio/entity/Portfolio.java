package com.portfolio.entity;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
public class Portfolio {
    private long id;
    private  User user;
    private List<Stock> stocks;
    private List<Cryptocurrency> cryptocurrencies;
    private List<Group> groups;

    public Portfolio(long id, User user, List<Stock> stocks, List<Cryptocurrency> cryptocurrencies, List<Group> groups) {
        this.id = id;
        this.user = user;
        this.stocks = stocks;
        this.cryptocurrencies = cryptocurrencies;
        this.groups = groups;
    }
}
