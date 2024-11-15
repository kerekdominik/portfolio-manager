package com.portfolio.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CryptoResponseDto {
    private Long id;
    private String name;
    private String symbol;
    private double quantity;
    private double price;
    private double originalValue;
    private double currentPrice;
    private double currentValue;
    private double pnl;
    private LocalDate purchaseDate;
    private String groupName;
}
