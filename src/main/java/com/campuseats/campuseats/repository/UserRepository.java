package com.campuseats.campuseats.repository;

import com.campuseats.campuseats.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    // JpaRepository already has methods like save(), findById(), etc.

}