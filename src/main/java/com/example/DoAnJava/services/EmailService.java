package com.example.DoAnJava.services;
import com.example.DoAnJava.entity.CartItem;
import com.example.DoAnJava.entity.EmailModel;
import com.example.DoAnJava.services.CartService;
import com.example.DoAnJava.services.EmailService;
import com.example.DoAnJava.services.OrderService;
import com.example.DoAnJava.services.PaymentMethodService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Service
public class EmailService {
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender javaMailSender;

    public void sendOrderConfirmationEmail(EmailModel emailModel) {
        logger.info("Sending order confirmation email to: " + emailModel.getTo());
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(emailModel.getTo());
        message.setSubject("Xác nhận đơn hàng của bạn");
        message.setText("Xin chào " + emailModel.getCustomerName() + ",\n\n" +
                "Cảm ơn bạn đã đặt hàng. Dưới đây là thông tin chi tiết đơn hàng của bạn:\n\n" +
                emailModel.getOrderDetails() + "\n\n" +
                "Tổng tiền: " + emailModel.getTotalAmount() + " VND\n\n" +
                "Phương thức thanh toán: " + emailModel.getPaymentMethod() + "\n" +
                "Ghi chú: " + emailModel.getNotes() + "\n" +
                "Địa chỉ giao hàng: " + emailModel.getShippingAddress() + "\n" +
                "Số điện thoại: " + emailModel.getPhoneNumber() + "\n" +
                "Email: " + emailModel.getEmail() + "\n" +
                "Trân trọng,\n" +
                "Đội ngũ hỗ trợ khách hàng");

        javaMailSender.send(message);
        logger.info("Order confirmation email sent to: " + emailModel.getTo());
    }
}