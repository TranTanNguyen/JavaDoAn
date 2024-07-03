package com.example.DoAnJava.controller;

import com.example.DoAnJava.entity.EmailModel;
import com.example.DoAnJava.services.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EmailController {
    @Autowired
    private EmailService emailService;

    @PostMapping("/placeOrder")
    public String placeOrder(
            @RequestParam("email") String email,
            @RequestParam("customerName") String customerName,
            @RequestParam("orderDetails") String orderDetails) {

        // Thực hiện logic đặt hàng
        // ...

        // Tạo EmailModel và gửi email xác nhận
        EmailModel emailModel = new EmailModel();
        emailModel.setTo(email);
        emailModel.setCustomerName(customerName);
        emailModel.setOrderDetails(orderDetails);

        emailService.sendOrderConfirmationEmail(emailModel);

        // Chuyển hướng tới trang xác nhận đơn hàng
        return "orderConfirmation";
    }
}