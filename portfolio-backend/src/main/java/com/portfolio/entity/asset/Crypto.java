package com.portfolio.entity.asset;

import com.portfolio.entity.enums.AssetCategory;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "cryptocurrency")
public class Crypto extends CommonAsset {
    {
        this.setAssetCategory(AssetCategory.CRYPTOCURRENCY);
    }
}
