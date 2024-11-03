package com.portfolio.entity.asset;

import com.portfolio.entity.enums.AssetCategory;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "cryptocurrency")
public class Crypto extends CommonAsset {

    private String externalId;

    public Crypto(String externalId, String name, String symbol) {
        super(name, symbol);
        this.externalId = externalId;
        this.setAssetCategory(AssetCategory.CRYPTOCURRENCY);
    }
}
