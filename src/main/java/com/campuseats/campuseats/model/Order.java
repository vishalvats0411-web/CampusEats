package com.campuseats.campuseats.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Data
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String collegeId;
    private String canteenName;
    private double totalAmount;
    private String status; // "PAID" or "PENDING"
    private LocalDateTime orderTime;
}