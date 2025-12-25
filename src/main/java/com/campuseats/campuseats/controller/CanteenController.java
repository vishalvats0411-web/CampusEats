package com.campuseats.campuseats.controller;

import com.campuseats.campuseats.model.MenuItem;
import com.campuseats.campuseats.repository.MenuItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Arrays;
import java.util.List;

@Controller
public class CanteenController {

    @GetMapping("/canteens")
    public String showCanteens(Model model) {
        // List of canteens as per your requirement
        List<String> canteens = Arrays.asList(
                "Ravi Canteen", "QBC", "Delicious Canteen",
                "CS/IT Canteen", "Gate 1 Canteen", "Gate 2 Canteen"
        );

        model.addAttribute("canteens", canteens);
        return "canteens"; // This will look for canteens.html
    }
    @Autowired
    private MenuItemRepository menuItemRepository;

    @GetMapping("/menu")
    public String showMenu(@RequestParam("name") String canteenName, Model model) {
        // Fetch items from database based on the canteen clicked
        List<MenuItem> items = menuItemRepository.findByCanteenName(canteenName);

        model.addAttribute("canteenName", canteenName);
        model.addAttribute("menuItems", items);
        return "menu"; // This will look for menu.html
    }
}