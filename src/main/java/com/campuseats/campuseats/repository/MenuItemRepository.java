package com.campuseats.campuseats.repository;

import com.campuseats.campuseats.model.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {
    // Custom query to find items belonging to a specific canteen
    List<MenuItem> findByCanteenName(String canteenName);
}