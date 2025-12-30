package com.campuseats.campuseats.repository;

import com.campuseats.campuseats.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByStatusNot(String status); // To show active orders (not "DELIVERED")
    List<Order> findAllByOrderByOrderTimeDesc(); // For history
    // Add inside OrderRepository interface
    List<Order> findByUser_CollegeIdOrderByOrderTimeDesc(String collegeId);
}