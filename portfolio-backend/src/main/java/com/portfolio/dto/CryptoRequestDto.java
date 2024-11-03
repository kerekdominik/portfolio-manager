package com.portfolio.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class CryptoRequestDto {
    private String id;
    private Long groupId;
    private double price;
    private double quantity;
    private LocalDate purchaseDate;
}
