package com.portfolio.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Group {
    @Id
    private Long id;
    private String name;

    @ManyToOne
    private Portfolio portfolio;

    public Group() {
        // TODO document why this constructor is empty
    }
}
