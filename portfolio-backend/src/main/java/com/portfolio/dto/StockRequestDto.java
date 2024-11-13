package com.portfolio.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class StockRequestDto {
    private String symbol;
    private String name;
    private String exchange;
    private double quantity;
    private double price;
    private LocalDate purchaseDate;
    private Long groupId;
}
