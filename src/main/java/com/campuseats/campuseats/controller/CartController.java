package com.campuseats.campuseats.controller;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.campuseats.campuseats.model.CartItem;
import com.campuseats.campuseats.model.MenuItem;
import com.campuseats.campuseats.model.Order;
import com.campuseats.campuseats.repository.MenuItemRepository;
import com.campuseats.campuseats.repository.OrderRepository;
import com.campuseats.campuseats.service.OrderService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final MenuItemRepository menuItemRepository;
    private final OrderRepository orderRepository;
    private final OrderService orderService; // Injected the updated Service Layer

    // Inject the public key to pass to the frontend
    @Value("${razorpay.key.id}")
    private String keyId;

    @SuppressWarnings("unchecked")
    private List<CartItem> getSessionCart(HttpSession session) {
        return (List<CartItem>) session.getAttribute("cart");
    }

    @PostMapping("/add")
    public String addToCart(@RequestParam Long itemId,
                            @RequestParam(required = false, defaultValue = "false") boolean forceClear,
                            HttpSession session,
                            RedirectAttributes redirectAttributes) {

        MenuItem item = menuItemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid menu item ID: " + itemId));

        List<CartItem> cart = getSessionCart(session);
        String currentCanteen = (String) session.getAttribute("currentCanteen");

        // Check for a mismatch IF the cart isn't empty
        if (cart != null && !cart.isEmpty() && !item.getCanteenName().equals(currentCanteen)) {
            if (!forceClear) {
                // Do not add the item. Instead, pass warning flags back to the frontend.
                redirectAttributes.addFlashAttribute("canteenMismatch", true);
                redirectAttributes.addFlashAttribute("pendingItemId", itemId);
                redirectAttributes.addFlashAttribute("existingCanteen", currentCanteen);
                redirectAttributes.addFlashAttribute("newCanteen", item.getCanteenName());
                redirectAttributes.addFlashAttribute("toastMessage", item.getName() + " added to cart! 🍔");
                return "redirect:/menu?name=" + item.getCanteenName();
            } else {
                // User confirmed they want to clear the cart
                cart = new ArrayList<>();
                session.setAttribute("currentCanteen", item.getCanteenName());
            }
        } else if (cart == null) {
            cart = new ArrayList<>();
            session.setAttribute("currentCanteen", item.getCanteenName());
        }

        // --- Standard logic to add item to the cart ---
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

        // Optional UX enhancement: Show a success message
        redirectAttributes.addFlashAttribute("successMessage", item.getName() + " added to cart!");
        return "redirect:/menu?name=" + item.getCanteenName();
    }

    @GetMapping("/checkout")
    public String showCheckout(HttpSession session, Model model) {
        List<CartItem> cart = getSessionCart(session);
        BigDecimal total = BigDecimal.ZERO;

        if (cart != null) {
            for (CartItem item : cart) {
                BigDecimal itemSubtotal = item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
                total = total.add(itemSubtotal);
            }
        }

        model.addAttribute("cart", cart);
        model.addAttribute("total", total);
        return "checkout";
    }

    @GetMapping("/payment")
    public String showPayment(HttpSession session, Principal principal, Model model) {
        if (principal == null) return "redirect:/login";

        List<CartItem> cart = getSessionCart(session);
        String canteen = (String) session.getAttribute("currentCanteen");

        if (cart == null || cart.isEmpty()) return "redirect:/canteens";

        try {
            // Delegate order creation entirely to the Service layer before the page loads
            Order order = orderService.createRazorpayOrder(principal.getName(), canteen, cart);

            // Pass necessary Razorpay data to the Thymeleaf template
            model.addAttribute("razorpayOrderId", order.getRazorpayOrderId());
            model.addAttribute("localOrderId", order.getId());
            model.addAttribute("total", order.getTotalAmount());
            model.addAttribute("keyId", keyId);

            return "payment";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Payment initiation failed: " + e.getMessage());
            return "error";
        }
    }

    @PostMapping("/verify-payment")
    public String verifyPayment(
            @RequestParam("razorpay_payment_id") String paymentId,
            @RequestParam("razorpay_signature") String signature,
            @RequestParam("local_order_id") Long localOrderId,
            HttpSession session,
            Model model) {

        try {
            // Delegate signature verification and status update to the Service layer
            Order order = orderService.verifyPaymentAndUpdateOrder(localOrderId, paymentId, signature);

            // Clear the session cart upon successful payment
            session.removeAttribute("cart");
            session.removeAttribute("currentCanteen");

            return "redirect:/cart/success?orderId=" + order.getId();
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Payment verification failed or was tampered with. Please contact support.");
            return "error";
        }
    }

    @GetMapping("/success")
    public String showSuccess(@RequestParam(required = false) Long orderId, Model model) {
        if (orderId != null) {
            Order order = orderRepository.findById(orderId).orElse(null);
            if (order != null) {
                model.addAttribute("otp", order.getOtp());
            }
        }
        return "success";
    }

    @GetMapping("/my-orders")
    public String showMyOrders(Principal principal, Model model) {
        String userId = principal.getName();
        List<Order> myOrders = orderRepository.findByUser_CollegeIdOrderByOrderTimeDesc(userId);
        model.addAttribute("orders", myOrders);
        return "my-orders";
    }
    @PostMapping("/update")
    public String updateCartItem(@RequestParam Long itemId, @RequestParam String action, HttpSession session) {
        List<CartItem> cart = getSessionCart(session);
        if (cart == null) return "redirect:/cart/checkout";

        cart.removeIf(item -> {
            if (item.getItemId().equals(itemId)) {
                if ("increase".equals(action)) {
                    item.setQuantity(item.getQuantity() + 1);
                } else if ("decrease".equals(action)) {
                    item.setQuantity(item.getQuantity() - 1);
                }
                // Remove item completely if action is 'remove' or quantity drops to 0
                return "remove".equals(action) || item.getQuantity() <= 0;
            }
            return false;
        });

        if (cart.isEmpty()) {
            session.removeAttribute("cart");
            session.removeAttribute("currentCanteen");
        } else {
            session.setAttribute("cart", cart);
        }

        return "redirect:/cart/checkout";
    }
}