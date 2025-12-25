package com.campuseats.campuseats.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "users")
@Data // This Lombok annotation generates getters, setters, and constructors automatically
public class User {
    @Id
    private String collegeId; // We use College ID as the Primary Key
    private String name;
    private String password;
}