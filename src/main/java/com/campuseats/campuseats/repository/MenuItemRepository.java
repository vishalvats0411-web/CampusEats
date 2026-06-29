package com.campuseats.campuseats.repository;

import com.campuseats.campuseats.model.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {
    List<MenuItem> findByCanteenName(String canteenName);

    // NEW: Search by keyword and category
    List<MenuItem> findByCanteenNameAndNameContainingIgnoreCase(String canteenName, String keyword);
    List<MenuItem> findByCanteenNameAndCategory(String canteenName, String category);
}