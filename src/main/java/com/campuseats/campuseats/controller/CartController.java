package com.campuseats.campuseats.controller;

import com.campuseats.campuseats.model.CartItem;
import com.campuseats.campuseats.model.MenuItem;
import com.campuseats.campuseats.model.Order;
import com.campuseats.campuseats.repository.MenuItemRepository;
import com.campuseats.campuseats.repository.OrderRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private MenuItemRepository menuItemRepository;

    @PostMapping("/add")
    public String addToCart(@RequestParam Long itemId, HttpSession session) {
        MenuItem item = menuItemRepository.findById(itemId).get();

        // Get the current cart from the session, or create a new one
        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
        String currentCanteen = (String) session.getAttribute("currentCanteen");

        if (cart == null || !item.getCanteenName().equals(currentCanteen)) {
            // New cart or different canteen selected -> Reset cart for the new canteen
            cart = new ArrayList<>();
            session.setAttribute("currentCanteen", item.getCanteenName());
        }

        // Check if item already exists in cart, if so, just increase quantity
        boolean exists = false;
        for (CartItem ci : cart) {
            if (ci.getItemId().equals(itemId)) {
                ci.setQuantity(ci.getQuantity() + 1);
                exists = true;
                break;
            }
        }

        if (!exists) {
            cart.add(new CartItem(item.getId(), item.getName(), item.getPrice(), 1));
        }

        session.setAttribute("cart", cart);
        return "redirect:/menu?name=" + item.getCanteenName();
    }

    @GetMapping("/checkout")
    public String showCheckout(HttpSession session, Model model) {
        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
        double total = 0;

        if (cart != null) {
            for (CartItem item : cart) {
                total += item.getPrice() * item.getQuantity();
            }
        }

        model.addAttribute("cart", cart);
        model.addAttribute("total", total);
        return "checkout";
    }
    @Autowired
    private OrderRepository orderRepository;

    @GetMapping("/payment")
    public String showPayment(HttpSession session, Model model) {
        Double total = (Double) model.getAttribute("total");
        // If total isn't in model, recalculate from session cart
        if (total == null) {
            List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
            total = cart.stream().mapToDouble(i -> i.getPrice() * i.getQuantity()).sum();
        }
        model.addAttribute("total", total);
        return "payment";
    }

    @PostMapping("/confirm-payment")
    public String confirmPayment(HttpSession session) {
        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
        String canteen = (String) session.getAttribute("currentCanteen");

        if (cart == null || cart.isEmpty()) {
            return "redirect:/canteens";
        }

        // 1. Calculate final total
        double total = cart.stream().mapToDouble(i -> i.getPrice() * i.getQuantity()).sum();

        // 2. Create and Save the Order to MySQL
        Order order = new Order();
        order.setCanteenName(canteen);
        order.setTotalAmount(total);
        order.setStatus("PAID");
        order.setOrderTime(LocalDateTime.now());
        // Use a dummy ID for now since we haven't linked the session User yet
        order.setCollegeId("STUDENT_123");

        orderRepository.save(order);

        // 3. Clear the cart session so they can start a new order later
        session.removeAttribute("cart");

        return "redirect:/cart/success";
    }
    @GetMapping("/success")
    public String showSuccess() {
        return "success"; // This will look for src/main/resources/templates/success.html
    }
}