package com.campuseats.campuseats.controller;

import com.campuseats.campuseats.model.CartItem;
import com.campuseats.campuseats.model.MenuItem;
import com.campuseats.campuseats.model.Order;
import com.campuseats.campuseats.model.OrderItem;
import com.campuseats.campuseats.repository.MenuItemRepository;
import com.campuseats.campuseats.repository.OrderRepository;
import com.campuseats.campuseats.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final MenuItemRepository menuItemRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    // FIX 2: Helper method to handle the "Unchecked cast" warning in one place
    @SuppressWarnings("unchecked")
    private List<CartItem> getSessionCart(HttpSession session) {
        return (List<CartItem>) session.getAttribute("cart");
    }

    @PostMapping("/add")
    public String addToCart(@RequestParam Long itemId, HttpSession session) {
        // FIX 1: Use .orElseThrow() instead of .get()
        MenuItem item = menuItemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid menu item ID: " + itemId));

        // Use the helper method
        List<CartItem> cart = getSessionCart(session);
        String currentCanteen = (String) session.getAttribute("currentCanteen");

        if (cart == null || !item.getCanteenName().equals(currentCanteen)) {
            cart = new ArrayList<>();
            session.setAttribute("currentCanteen", item.getCanteenName());
        }

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
        List<CartItem> cart = getSessionCart(session); // Use helper method
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

    @GetMapping("/payment")
    public String showPayment(HttpSession session, Model model) {
        Double total = (Double) model.getAttribute("total");

        if (total == null) {
            List<CartItem> cart = getSessionCart(session); // Use helper method
            // Added check to prevent NullPointerException if cart is empty
            if (cart != null) {
                total = cart.stream().mapToDouble(i -> i.getPrice() * i.getQuantity()).sum();
            } else {
                total = 0.0;
            }
        }
        model.addAttribute("total", total);
        return "payment";
    }

    @PostMapping("/confirm-payment")
    public String confirmPayment(HttpSession session, Principal principal) {
        if (principal == null) return "redirect:/login";

        List<CartItem> cart = getSessionCart(session); // Use helper method
        String canteen = (String) session.getAttribute("currentCanteen");

        if (cart == null || cart.isEmpty()) return "redirect:/canteens";

        com.campuseats.campuseats.model.User user = userRepository.findById(principal.getName()).orElseThrow();

        Order order = new Order();
        order.setUser(user);
        order.setCanteenName(canteen);
        order.setStatus("PAID");
        order.setOrderTime(LocalDateTime.now());

        double total = 0;
        for (CartItem ci : cart) {
            OrderItem orderItem = new OrderItem(ci.getItemName(), ci.getPrice(), ci.getQuantity(), order);
            order.getOrderItems().add(orderItem);
            total += ci.getPrice() * ci.getQuantity();
        }
        order.setTotalAmount(total);

        orderRepository.save(order);

        session.removeAttribute("cart");
        return "redirect:/cart/success";
    }

    @GetMapping("/success")
    public String showSuccess() {
        return "success";
    }

    @GetMapping("/my-orders")
    public String showMyOrders(Principal principal, Model model) {
        String userId = principal.getName();
        List<Order> myOrders = orderRepository.findByUser_CollegeIdOrderByOrderTimeDesc(userId);
        model.addAttribute("orders", myOrders);
        return "my-orders";
    }
}