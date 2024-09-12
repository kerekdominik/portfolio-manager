package com.portfolio.entity;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
 @Getter
@Setter
public class Stock {
    private long id;
    private String symbol;

}
