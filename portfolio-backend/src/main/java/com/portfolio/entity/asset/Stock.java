package com.portfolio.entity.asset;

import com.portfolio.entity.enums.AssetCategory;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "stock")
public class Stock extends CommonAsset {

    private String exchange;

    public Stock(String symbol, String name, String exchange) {
        super(name, symbol);
        this.exchange = exchange;
        this.setAssetCategory(AssetCategory.STOCK);
    }
}
