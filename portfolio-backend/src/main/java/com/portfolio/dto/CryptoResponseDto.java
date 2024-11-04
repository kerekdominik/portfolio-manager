package com.portfolio.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CryptoResponseDto {
    private Long id;
    private String name;
    private String symbol;
    private double currentPrice;
    private double price;
    private double quantity;
    private LocalDate purchaseDate;
    private String groupName;
}
