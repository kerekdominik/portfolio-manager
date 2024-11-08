package com.portfolio.entity.asset.external;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "\"stock_list\"")
public class StockListItem {

    @Id
    private String symbol; // Unique symbol for each stock

    private String name;
    private String exchange;
    private String assetType;
    private String status;
}
