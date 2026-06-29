package com.campuseats.campuseats.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
@Entity
@Table(name = "order_items")
@Data
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String itemName;
    private BigDecimal price; // Price at the time of purchase
    private int quantity;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    // Constructors
    public OrderItem() {}

    public OrderItem(String itemName, BigDecimal price, int quantity, Order order) {
        this.itemName = itemName;
        this.price = price;
        this.quantity = quantity;
        this.order = order;
    }
}