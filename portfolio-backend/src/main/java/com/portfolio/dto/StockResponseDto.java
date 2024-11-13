package com.portfolio.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class StockResponseDto {
    private Long id;
    private String symbol;
    private String name;
    private String exchange;
    private double quantity;
    private double price;
    private double currentPrice;
    private LocalDate purchaseDate;
    private String groupName;
}
