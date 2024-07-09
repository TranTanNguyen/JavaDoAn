package com.example.DoAnJava.controller;

import com.example.DoAnJava.entity.*;
import com.example.DoAnJava.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @Autowired
    private CartService cartService;

    @Autowired
    private PaymentMethodService paymentMethodService;

    @Autowired
    private EmailService emailService;

    @GetMapping("/checkout")
    public String checkout(Model model) {
        model.addAttribute("paymentMethods", paymentMethodService.getAllPaymentMethods());
        return "cart/checkout";
    }

    @PostMapping("/submit")
    public String submitOrder(@RequestParam("customerName") String customerName,
                              @RequestParam("shippingAddress") String shippingAddress,
                              @RequestParam("phoneNumber") String phoneNumber,
                              @RequestParam("email") String email,
                              @RequestParam("notes") String notes,
                              @RequestParam("paymentMethodId") Long paymentMethodId,
                              Model model) {
        List<CartItem> cartItems = cartService.getCartItems();
        if (cartItems.isEmpty()) {
            return "redirect:/cart"; // Redirect if cart is empty
        }
        try {
            // Get payment method information
            Optional<PaymentMethod> paymentMethodOptional = paymentMethodService.getPaymentMethodById(paymentMethodId);
            if (!paymentMethodOptional.isPresent()) {
                model.addAttribute("error", "Phương thức thanh toán không hợp lệ.");
                return "cart/checkout"; // Redirect to checkout page with error message
            }
            PaymentMethod paymentMethod = paymentMethodOptional.get();

            // Create order details string from cartItems
            String orderDetails = cartItems.stream()
                    .map(CartItem::toString)
                    .collect(Collectors.joining("\n"));

            // Create order
            Order order = orderService.createOrder(customerName, shippingAddress, phoneNumber, email, notes, paymentMethodId, cartItems);

            // Prepare email model
            EmailModel emailModel = new EmailModel();
            emailModel.setTo(email);
            emailModel.setCustomerName(customerName);
            emailModel.setOrderDetails(orderDetails);
            emailModel.setTotalAmount(order.getTotalAmount()); // Assuming getTotalAmount method exists in Order entity
            emailModel.setPaymentMethod(paymentMethod.getName());
            emailModel.setNotes(notes != null ? notes : "Không có ghi chú");
            emailModel.setShippingAddress(shippingAddress);
            emailModel.setPhoneNumber(phoneNumber);
            emailModel.setEmail(email);

            // Send order confirmation email
            emailService.sendOrderConfirmationEmail(emailModel);

            return "redirect:/order/confirmation"; // Redirect to order confirmation page
        } catch (Exception e) {
            model.addAttribute("error", "Đã có lỗi xảy ra khi đặt hàng.");
            return "cart/checkout"; // Redirect to checkout page with error message
        }
    }

    @GetMapping("/confirmation")
    public String orderConfirmation(Model model) {
        model.addAttribute("message", "Đơn hàng của bạn đã được đặt thành công.");
        return "cart/order-confirmation";
    }

    @GetMapping("/admin/orders/{id}")
    public String showOrderDetail(@PathVariable Long id, Model model) {
        Order order = orderService.getOrderById(id);
        model.addAttribute("order", order);
        return "admin/order-detail";
    }
}
