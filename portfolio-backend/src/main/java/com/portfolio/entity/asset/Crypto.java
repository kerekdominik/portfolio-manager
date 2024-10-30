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
    @Enumerated(EnumType.STRING)
    private AssetCategory category = AssetCategory.CRYPTOCURRENCY;
}
