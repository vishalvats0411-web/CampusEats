package com.campuseats.campuseats.controller;

import com.campuseats.campuseats.model.User;
import com.campuseats.campuseats.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/register")
    public String showRegistrationForm() {
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(User user) {
        userRepository.save(user);
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    @PostMapping("/login")
    public String loginUser(@RequestParam String collegeId,
                            @RequestParam String password,
                            Model model) {

        // 1. Look for the user by their College ID
        Optional<User> user = userRepository.findById(collegeId);

        // 2. Logic to check if user exists and password is correct
        if (user.isPresent() && user.get().getPassword().equals(password)) {
            // Success! For now, we'll redirect to a "canteen-selection" page (Feature 2)
            return "redirect:/canteens";
        } else {
            // Fail! Send them back to login with an error message
            model.addAttribute("error", "Invalid College ID or Password");
            return "login";
        }
    }
}