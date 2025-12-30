package com.campuseats.campuseats.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    @NotBlank(message = "College ID is required")
    private String collegeId;

    @NotBlank(message = "Name cannot be empty")
    private String name;

    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;
    private String role;
}