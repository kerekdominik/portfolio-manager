package com.portfolio.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "\"group\"")
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PortfolioAsset> assets;

    // Helper method to manage bidirectional relationship
    public void addAsset(PortfolioAsset asset) {
        assets.add(asset);
        asset.setGroup(this);
    }

    public void removeAsset(PortfolioAsset asset) {
        assets.remove(asset);
        asset.setGroup(null);
    }

    // Constructor for creating a group with a user
    public Group(String name, User user) {
        this.name = name;
        this.user = user;
    }
}
