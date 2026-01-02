package com.campuseats.campuseats.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Entity
@Table(name = "menu_items")
@Data
public class MenuItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Item name is required")
    private String name;

    @Positive(message = "Price must be greater than zero")
    private double price;

    @NotBlank(message = "Canteen name is required")
    private String canteenName;

    // --- NEW FIELDS ADDED ---
    @Column(length = 500) // Allow longer descriptions
    private String description;

    private String imageUrl;

    private String category; // e.g., "Breakfast", "Lunch"

    public MenuItem() {}

    // Updated Constructor
    public MenuItem(String name, double price, String canteenName, String description, String imageUrl, String category) {
        this.name = name;
        this.price = price;
        this.canteenName = canteenName;
        this.description = description;
        this.imageUrl = imageUrl;
        this.category = category;
    }

    // Old Constructor for backward compatibility (optional)
    public MenuItem(String name, double price, String canteenName) {
        this.name = name;
        this.price = price;
        this.canteenName = canteenName;
    }
}