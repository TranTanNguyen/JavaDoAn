package com.example.DoAnJava.services;

import com.example.DoAnJava.entity.CartItem;
import com.example.DoAnJava.entity.Order;
import com.example.DoAnJava.entity.OrderDetail;
import com.example.DoAnJava.entity.PaymentMethod;
import com.example.DoAnJava.repository.OrderDetailRepository;
import com.example.DoAnJava.repository.OrderRepository;
import com.example.DoAnJava.repository.PaymentMethodRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private CartService cartService;

    @Autowired
    private PaymentMethodRepository paymentMethodRepository;

    @Transactional
    public Order createOrder(String customerName, String shippingAddress, String phoneNumber, String email, String notes, Long paymentMethodId, List<CartItem> cartItems) {
        PaymentMethod paymentMethod = paymentMethodRepository.findById(paymentMethodId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid payment method Id:" + paymentMethodId));

        Order order = new Order();
        order.setCustomerName(customerName);
        order.setShippingAddress(shippingAddress);
        order.setPhoneNumber(phoneNumber);
        order.setEmail(email);
        order.setNotes(notes);
        order.setPaymentMethod(paymentMethod.getName());
        order = orderRepository.save(order);
        for (CartItem item : cartItems) {
            OrderDetail detail = new OrderDetail();
            detail.setOrder(order);
            detail.setProduct(item.getProduct());
            detail.setQuantity(item.getQuantity());
            orderDetailRepository.save(detail);
        }
        // Optionally clear the cart after order placement
        cartService.clearCart();
        return order;
    }
}