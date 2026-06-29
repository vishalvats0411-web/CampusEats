package com.campuseats.campuseats.model;

import lombok.Data;
import java.math.BigDecimal;
@Data
public class CartItem {
    private Long itemId;
    private String itemName;
    private BigDecimal price;
    private int quantity;

    public CartItem(Long itemId, String itemName, BigDecimal price, int quantity) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.price = price;
        this.quantity = quantity;
    }
}