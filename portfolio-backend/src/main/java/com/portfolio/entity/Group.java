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
@Table(name = "\"group\"")
public class Group {
    @Id
    private Long id;
    private String name;

    @ManyToOne
    private Portfolio portfolio;

    public Group() {

    }

    public Group(long id, String name, Portfolio portfolio) {
        this.id = id;
        this.name = name;
        this.portfolio = portfolio;
    }
}
