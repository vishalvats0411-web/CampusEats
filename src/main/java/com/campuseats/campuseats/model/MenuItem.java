package com.campuseats.campuseats.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "menu_items")
@Data
public class MenuItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private double price;
    private String canteenName; // Used to filter items (e.g., "Ravi Canteen")
    // 1. You MUST have an empty constructor for JPA/Hibernate to work
    public MenuItem() {}

    // 2. Add this constructor so your DataInitializer works
    public MenuItem(String name, double price, String canteenName) {
        this.name = name;
        this.price = price;
        this.canteenName = canteenName;
    }
}