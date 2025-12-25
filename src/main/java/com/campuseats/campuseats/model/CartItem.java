package com.campuseats.campuseats.model;

import lombok.Data;

@Data
public class CartItem {
    private Long itemId;
    private String itemName;
    private double price;
    private int quantity;

    public CartItem(Long itemId, String itemName, double price, int quantity) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.price = price;
        this.quantity = quantity;
    }
}