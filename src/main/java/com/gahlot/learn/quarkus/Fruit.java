package com.gahlot.learn.quarkus;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor  // required for JSON serialization
public class Fruit {
    private String name;
    private String description;
    private String id;

    public Fruit(String name, String description) {
        this.name = name;
        this.description = description;
    }
}