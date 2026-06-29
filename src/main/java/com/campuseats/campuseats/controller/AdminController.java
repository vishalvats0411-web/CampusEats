package com.campuseats.campuseats.controller;

import com.campuseats.campuseats.model.Canteen;
import com.campuseats.campuseats.model.MenuItem;
import com.campuseats.campuseats.model.OrderStatus;
import com.campuseats.campuseats.repository.CanteenRepository;
import com.campuseats.campuseats.repository.MenuItemRepository;
import com.campuseats.campuseats.repository.OrderRepository;
import com.campuseats.campuseats.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final OrderRepository orderRepository;
    private final MenuItemRepository menuItemRepository;
    private final CanteenRepository canteenRepository;
    private final OrderService orderService;

    // --- DASHBOARD ---
    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        model.addAttribute("orders", orderRepository.findByStatusNotIn(Arrays.asList(OrderStatus.DELIVERED, OrderStatus.CANCELLED)));
        return "admin/dashboard";
    }

    @PostMapping("/order/{id}/status")
    public String updateStatus(@PathVariable Long id, @RequestParam OrderStatus status) {
        orderService.updateOrderStatus(id, status);
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/order/{id}/deliver")
    public String markDelivered(@PathVariable Long id, @RequestParam String otp, Model model) {
        boolean isDelivered = orderService.verifyOtpAndDeliver(id, otp);
        if (isDelivered) {
            return "redirect:/admin/dashboard";
        } else {
            model.addAttribute("error", "Invalid OTP for Order #" + id);
            model.addAttribute("orders", orderRepository.findByStatusNotIn(Arrays.asList(OrderStatus.DELIVERED, OrderStatus.CANCELLED)));
            return "admin/dashboard";
        }
    }

    // --- MANAGE MENU & CANTEENS ---
    @GetMapping("/manage-menu")
    public String showManageMenu(Model model) {
        model.addAttribute("canteens", canteenRepository.findAll());
        model.addAttribute("menuItems", menuItemRepository.findAll());
        return "admin/manage-menu";
    }

    @GetMapping("/add-canteen")
    public String showAddCanteenForm() {
        return "admin/add-canteen";
    }

    @PostMapping("/add-canteen")
    public String addCanteen(@RequestParam String name,
                             @RequestParam String description,
                             @RequestParam String location,
                             @RequestParam String imageUrl) {
        Canteen newCanteen = new Canteen();
        newCanteen.setName(name);
        newCanteen.setDescription(description);
        newCanteen.setLocation(location);
        newCanteen.setImageUrl(imageUrl);
        canteenRepository.save(newCanteen);
        return "redirect:/admin/manage-menu";
    }

    @PostMapping("/canteen/{id}/delete")
    public String deleteCanteen(@PathVariable Long id) {
        canteenRepository.deleteById(id);
        return "redirect:/admin/manage-menu";
    }

    @PostMapping("/item/{id}/delete")
    public String deleteMenuItem(@PathVariable Long id) {
        menuItemRepository.deleteById(id);
        return "redirect:/admin/manage-menu";
    }

    @PostMapping("/item/{id}/toggle")
    public String toggleItemStock(@PathVariable Long id) {
        MenuItem item = menuItemRepository.findById(id).orElseThrow();
        item.setAvailable(!item.isAvailable());
        menuItemRepository.save(item);
        return "redirect:/admin/manage-menu";
    }

    // --- ADD ITEM (WITH MULTIPLE CANTEENS & IMAGE UPLOAD) ---
    @GetMapping("/add-item")
    public String showAddItemForm(Model model) {
        model.addAttribute("canteens", canteenRepository.findAll());
        return "admin/add-item";
    }

    @PostMapping("/add-item")
    public String addMenuItem(
            @RequestParam String name,
            @RequestParam BigDecimal price,
            @RequestParam String description,
            @RequestParam String category,
            @RequestParam List<String> canteenNames,
            @RequestParam("imageFile") MultipartFile imageFile) {

        String imageUrl = "/images/default.jpg";

        if (!imageFile.isEmpty()) {
            try {
                String uploadDir = "uploads/";
                Path uploadPath = Paths.get(uploadDir);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }
                String fileName = UUID.randomUUID().toString() + "_" + imageFile.getOriginalFilename();
                Path filePath = uploadPath.resolve(fileName);
                Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                imageUrl = "/uploads/" + fileName;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        for (String canteenName : canteenNames) {
            MenuItem newItem = new MenuItem();
            newItem.setName(name);
            newItem.setPrice(price);
            newItem.setDescription(description);
            newItem.setCategory(category);
            newItem.setCanteenName(canteenName);
            newItem.setImageUrl(imageUrl);
            menuItemRepository.save(newItem);
        }

        return "redirect:/admin/manage-menu";
    }
}