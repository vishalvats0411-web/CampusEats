package com.campuseats.campuseats.service;

import com.campuseats.campuseats.model.*;
import com.campuseats.campuseats.repository.OrderRepository;
import com.campuseats.campuseats.repository.UserRepository;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    // Injecting the Razorpay API keys from application.properties
    @Value("${razorpay.key.id}")
    private String keyId;

    @Value("${razorpay.key.secret}")
    private String keySecret;

    @Transactional
    public Order createRazorpayOrder(String collegeId, String canteenName, List<CartItem> cart) throws RazorpayException {
        User user = userRepository.findById(collegeId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // 1. Initialize local order with PENDING status
        Order localOrder = new Order();
        localOrder.setUser(user);
        localOrder.setCanteenName(canteenName);
        localOrder.setStatus(OrderStatus.PENDING); // Money is not received yet
        localOrder.setOrderTime(LocalDateTime.now());

        // 2. Build order items and calculate the total amount
        BigDecimal total = BigDecimal.ZERO;
        for (CartItem ci : cart) {
            OrderItem orderItem = new OrderItem(ci.getItemName(), ci.getPrice(), ci.getQuantity(), localOrder);
            localOrder.getOrderItems().add(orderItem);
            total = total.add(ci.getPrice().multiply(BigDecimal.valueOf(ci.getQuantity())));
        }
        localOrder.setTotalAmount(total);

        // 3. Initialize Razorpay Client
        RazorpayClient razorpay = new RazorpayClient(keyId, keySecret);

        // 4. Create Order Request for Razorpay (Amount must be passed in paise)
        int amountInPaise = total.multiply(new BigDecimal("100")).intValue();
        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", amountInPaise);
        orderRequest.put("currency", "INR");
        orderRequest.put("receipt", "txn_" + System.currentTimeMillis());

        // 5. Create order on Razorpay servers
        com.razorpay.Order razorpayOrder = razorpay.orders.create(orderRequest);

        // 6. Save the Razorpay Order ID to verify the payment later
        localOrder.setRazorpayOrderId(razorpayOrder.get("id"));

        return orderRepository.save(localOrder);
    }

    @Transactional
    public Order verifyPaymentAndUpdateOrder(Long localOrderId, String razorpayPaymentId, String razorpaySignature) {
        Order order = orderRepository.findById(localOrderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        try {
            // 1. Create a JSON object with the expected verification parameters
            JSONObject options = new JSONObject();
            options.put("razorpay_order_id", order.getRazorpayOrderId());
            options.put("razorpay_payment_id", razorpayPaymentId);
            options.put("razorpay_signature", razorpaySignature);

            // 2. Verify the signature cryptographically using your secret key
            boolean isValid = Utils.verifyPaymentSignature(options, keySecret);

            if (isValid) {
                // 3. If valid, mark as paid and generate the pickup OTP
                order.setStatus(OrderStatus.PAID);
                order.setRazorpayPaymentId(razorpayPaymentId);
                order.setRazorpaySignature(razorpaySignature);
                order.generateOtp();
                return orderRepository.save(order);
            } else {
                throw new RuntimeException("Payment Verification Failed: Invalid Signature");
            }
        } catch (RazorpayException e) {
            throw new RuntimeException("Error verifying payment signature", e);
        }
    }

    // --- Admin and Canteen Management Methods (Unchanged) ---

    @Transactional
    public void updateOrderStatus(Long orderId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
        order.setStatus(newStatus);
        orderRepository.save(order);
    }

    @Transactional
    public boolean verifyOtpAndDeliver(Long orderId, String otp) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        if (order.getOtp() != null && order.getOtp().equals(otp)) {
            order.setStatus(OrderStatus.DELIVERED);
            orderRepository.save(order);
            return true;
        }
        return false;
    }
}