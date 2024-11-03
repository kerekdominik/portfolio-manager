package com.portfolio.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CryptoResponseDto {
    private Long id;
    private String name;
    private String symbol;
    private double quantity;
    private LocalDateTime purchaseDate;
    private String groupName;
}
