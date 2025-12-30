package com.campuseats.campuseats.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "canteens")
@Data
public class Canteen {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    private String description; // Optional: e.g., "North Indian & Snacks"

    // Default constructor
    public Canteen() {}

    public Canteen(String name, String description) {
        this.name = name;
        this.description = description;
    }
}