package com.example.DoAnJava.controller;

import com.example.DoAnJava.entity.CartItem;
import com.example.DoAnJava.entity.EmailModel;
import com.example.DoAnJava.entity.PaymentMethod;
import com.example.DoAnJava.services.*;
import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
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

    @PostMapping("/confirm")
    public String confirmOrder(@ModelAttribute EmailModel emailModel, Model model) {
        try {
            // Xử lý logic xác nhận đặt hàng ở đây (nếu cần)

            // Gửi email xác nhận đặt hàng
            emailService.sendOrderConfirmationEmail(emailModel);

            model.addAttribute("success", true);
            return "orderConfirmationPage"; // Chuyển hướng đến trang xác nhận đơn hàng thành công
        } catch (Exception e) {
            model.addAttribute("error", "Đã có lỗi xảy ra khi xác nhận đơn hàng.");
            return "orderConfirmationPage"; // Hoặc có thể chuyển hướng đến trang lỗi
        }
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
            // Lấy thông tin phương thức thanh toán
            Optional<PaymentMethod> paymentMethodOptional = paymentMethodService.getPaymentMethodById(paymentMethodId);
            if (!paymentMethodOptional.isPresent()) {
                model.addAttribute("error", "Phương thức thanh toán không hợp lệ.");
                return "cart/checkout"; // Chuyển hướng đến trang checkout với thông báo lỗi
            }
            PaymentMethod paymentMethod = paymentMethodOptional.get();

            // Tạo chuỗi thông tin đơn hàng từ cartItems
            String orderDetails = cartItems.stream()
                    .map(CartItem::toString)
                    .collect(Collectors.joining("\n"));

            // Gửi email xác nhận đặt hàng
            EmailModel emailModel = new EmailModel();
            emailModel.setTo(email);
            emailModel.setCustomerName(customerName);
            emailModel.setOrderDetails(orderDetails);
            emailModel.setPaymentMethod(paymentMethod.getName());
            emailModel.setNotes(notes); // Set notes

            emailService.sendOrderConfirmationEmail(emailModel);

            // Tạo đơn hàng
            orderService.createOrder(customerName, shippingAddress, phoneNumber, email, notes, paymentMethodId, cartItems);

            return "redirect:/order/confirmation"; // Chuyển hướng đến trang xác nhận đơn hàng
        } catch (Exception e) {
            model.addAttribute("error", "Đã có lỗi xảy ra khi đặt hàng.");
            return "cart/checkout"; // Chuyển hướng đến trang checkout với thông báo lỗi
        }
    }

    @GetMapping("/confirmation")
    public String orderConfirmation(Model model) {
        model.addAttribute("message", "Đơn hàng của bạn đã được đặt thành công.");
        return "cart/order-confirmation";
    }
}