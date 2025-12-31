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

    public MenuItem() {}

    public MenuItem(String name, double price, String canteenName) {
        this.name = name;
        this.price = price;
        this.canteenName = canteenName;
    }
}