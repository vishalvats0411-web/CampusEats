package com.campuseats.campuseats.controller;

import com.campuseats.campuseats.model.Order;
import com.campuseats.campuseats.repository.OrderRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.campuseats.campuseats.model.MenuItem;
import com.campuseats.campuseats.repository.MenuItemRepository;
import com.campuseats.campuseats.repository.CanteenRepository;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final OrderRepository orderRepository;
    private final MenuItemRepository menuItemRepository;
    private final CanteenRepository canteenRepository;

    @GetMapping("/add-item")
    public String showAddItemForm(Model model) {
        model.addAttribute("menuItem", new MenuItem());
        model.addAttribute("canteens", canteenRepository.findAll()); // Populate dropdown
        return "admin/add-item";
    }

    @PostMapping("/add-item")
    public String addMenuItem(@Valid MenuItem menuItem, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("canteens", canteenRepository.findAll());
            return "admin/add-item";
        }
        menuItemRepository.save(menuItem);
        return "redirect:/admin/dashboard";
    }
    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        // Fetch all orders that are NOT Delivered yet
        model.addAttribute("orders", orderRepository.findByStatusNot("DELIVERED"));
        return "admin/dashboard";
    }

    @PostMapping("/order/{id}/status")
    public String updateStatus(@PathVariable Long id, @RequestParam String status) {
        Order order = orderRepository.findById(id).orElseThrow();
        order.setStatus(status);
        orderRepository.save(order);
        return "redirect:/admin/dashboard";
    }
}