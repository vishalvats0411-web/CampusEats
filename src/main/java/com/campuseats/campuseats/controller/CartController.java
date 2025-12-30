package com.campuseats.campuseats.controller;

import com.campuseats.campuseats.model.CartItem;
import com.campuseats.campuseats.model.MenuItem;
import com.campuseats.campuseats.model.Order;
import com.campuseats.campuseats.model.OrderItem;
import com.campuseats.campuseats.repository.MenuItemRepository;
import com.campuseats.campuseats.repository.OrderRepository;
import com.campuseats.campuseats.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private MenuItemRepository menuItemRepository;

    @Autowired
    private UserRepository userRepository;

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
    public String confirmPayment(HttpSession session, Principal principal) {
        if (principal == null) return "redirect:/login";

        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
        String canteen = (String) session.getAttribute("currentCanteen");

        if (cart == null || cart.isEmpty()) return "redirect:/canteens";

        // 1. Fetch the real User entity
        com.campuseats.campuseats.model.User user = userRepository.findById(principal.getName()).orElseThrow();

        // 2. Create Order
        Order order = new Order();
        order.setUser(user); // Link to User
        order.setCanteenName(canteen);
        order.setStatus("PAID");
        order.setOrderTime(LocalDateTime.now());

        // 3. Convert CartItems to OrderItems
        double total = 0;
        for (CartItem ci : cart) {
            OrderItem orderItem = new OrderItem(ci.getItemName(), ci.getPrice(), ci.getQuantity(), order);
            order.getOrderItems().add(orderItem);
            total += ci.getPrice() * ci.getQuantity();
        }
        order.setTotalAmount(total);

        // 4. Save (CascadeType.ALL will automatically save the OrderItems too)
        orderRepository.save(order);

        session.removeAttribute("cart");
        return "redirect:/cart/success";
    }
    @GetMapping("/success")
    public String showSuccess() {
        return "success"; // This will look for src/main/resources/templates/success.html
    }

    @GetMapping("/my-orders")
    public String showMyOrders(Principal principal, Model model) {
        String userId = principal.getName();
        List<Order> myOrders = orderRepository.findByUser_CollegeIdOrderByOrderTimeDesc(userId);
        model.addAttribute("orders", myOrders);
        return "my-orders";
    }
}