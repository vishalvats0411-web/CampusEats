package com.campuseats.campuseats.repository;

import com.campuseats.campuseats.model.Canteen;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CanteenRepository extends JpaRepository<Canteen, Long> {
}