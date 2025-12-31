package com.campuseats.campuseats.controller;

import com.campuseats.campuseats.model.User;
import com.campuseats.campuseats.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model; // Ensure Model is imported
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid User user, BindingResult result) {
        // 1. Check for validation errors (e.g., blank name, short password)
        if (result.hasErrors()) {
            return "register"; // Return to the form to show errors
        }

        // 2. Hash password and save
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole("ROLE_USER"); // Default role
        userRepository.save(user);

        return "redirect:/login?success";
    }

    @GetMapping("/login")
    public String showLoginForm(@RequestParam(value = "error", required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("error", "Invalid Credentials.");
        }
        return "login";
    }
    @GetMapping("/admin/login")
    public String showAdminLoginForm(@RequestParam(value = "error", required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("error", "Access Denied or Invalid Credentials.");
        }
        return "admin-login"; // matches template name
    }

    // REMOVE the manual @PostMapping("/login") method entirely.
    // Spring Security now handles authentication automatically.
}