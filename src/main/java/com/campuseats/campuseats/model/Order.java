package com.campuseats.campuseats.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Data
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // FIX 1: Relationship to User instead of plain String
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String canteenName;
    private double totalAmount;
    private String status;
    private LocalDateTime orderTime;

    // FIX 2: List of items in this order
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems = new ArrayList<>();
}