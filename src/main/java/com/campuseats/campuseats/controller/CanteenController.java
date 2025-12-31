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
        // FETCH from Database
        List<Canteen> canteenList = canteenRepository.findAll();

        // Extract just the names to match your current Thymeleaf template expectation
        // Or update the template to use Canteen objects directly (Better approach)
        List<String> canteenNames = canteenList.stream()
                .map(Canteen::getName)
                .collect(Collectors.toList());

        model.addAttribute("canteens", canteenNames);
        return "canteens";
    }

    @GetMapping("/menu")
    public String showMenu(@RequestParam("name") String canteenName, Model model) {
        List<MenuItem> items = menuItemRepository.findByCanteenName(canteenName);
        model.addAttribute("canteenName", canteenName);
        model.addAttribute("menuItems", items);
        return "menu";
    }
}