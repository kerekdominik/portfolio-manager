package com.portfolio.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class CryptoRequestDto {
    private String id;
    private Long groupId;
    private double price;
    private double quantity;
    private LocalDateTime purchaseDate;
}
