package com.campuseats.campuseats.repository;

import com.campuseats.campuseats.model.Order;
import com.campuseats.campuseats.model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByStatusNotIn(List<OrderStatus> statuses);
    List<Order> findByUser_CollegeIdOrderByOrderTimeDesc(String collegeId);
}