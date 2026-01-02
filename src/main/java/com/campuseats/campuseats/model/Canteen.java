package com.campuseats.campuseats.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "canteens")
@Data
public class Canteen {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    private String description;

    // --- NEW FIELDS ADDED ---
    private String location;
    private String imageUrl;

    // This allows us to save a Canteen AND its items in one go
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "canteen_id") // This creates a link in the menu_items table
    private List<MenuItem> menuItems = new ArrayList<>();

    // Default constructor
    public Canteen() {}

    public Canteen(String name, String description) {
        this.name = name;
        this.description = description;
    }
}