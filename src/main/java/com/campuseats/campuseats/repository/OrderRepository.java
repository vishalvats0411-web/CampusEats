package com.campuseats.campuseats.repository;

import com.campuseats.campuseats.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}