package com.campuseats.campuseats.controller;

import com.campuseats.campuseats.model.User;
import com.campuseats.campuseats.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final UserRepository userRepository;

    @GetMapping
    public String showProfile(Principal principal, Model model) {
        User user = userRepository.findById(principal.getName()).orElseThrow();
        model.addAttribute("user", user);
        return "profile";
    }

    @PostMapping("/update")
    public String updateProfile(Principal principal,
                                @RequestParam String name,
                                @RequestParam String dietaryPreference,
                                RedirectAttributes redirectAttributes) {
        User user = userRepository.findById(principal.getName()).orElseThrow();
        user.setName(name);
        user.setDietaryPreference(dietaryPreference);
        userRepository.save(user);

        redirectAttributes.addFlashAttribute("success", "Profile updated successfully!");
        return "redirect:/profile";
    }
}