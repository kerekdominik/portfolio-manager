package com.portfolio.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class CryptoRequestDto {
    private Long assetId;
    private Long groupId;
    private double quantity;
    private LocalDateTime purchaseDate;
}
