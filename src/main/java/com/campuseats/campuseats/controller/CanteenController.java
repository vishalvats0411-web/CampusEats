package com.campuseats.campuseats.controller;

import com.campuseats.campuseats.model.Canteen;
import com.campuseats.campuseats.model.MenuItem;
import com.campuseats.campuseats.repository.CanteenRepository;
import com.campuseats.campuseats.repository.MenuItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor

public class CanteenController {

    private final CanteenRepository canteenRepository;
    private final MenuItemRepository menuItemRepository;

    @GetMapping("/canteens")
    public String showCanteens(Model model) {
        // FETCH all Canteen objects from Database
        List<Canteen> canteenList = canteenRepository.findAll();

        // Pass the FULL objects to the template, not just the names
        model.addAttribute("canteens", canteenList);
        return "canteens";
    }

    @GetMapping("/menu")
    public String showMenu(
            @RequestParam("name") String canteenName,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "category", required = false) String category,
            Model model) {

        List<MenuItem> items;

        if (search != null && !search.isEmpty()) {
            items = menuItemRepository.findByCanteenNameAndNameContainingIgnoreCase(canteenName, search);
        } else if (category != null && !category.isEmpty()) {
            items = menuItemRepository.findByCanteenNameAndCategory(canteenName, category);
        } else {
            items = menuItemRepository.findByCanteenName(canteenName);
        }

        model.addAttribute("canteenName", canteenName);
        model.addAttribute("menuItems", items);
        return "menu";
    }
}