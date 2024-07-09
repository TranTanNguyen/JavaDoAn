package com.example.DoAnJava.services;

import com.example.DoAnJava.entity.*;
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
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final CartService cartService;
    private final PaymentMethodRepository paymentMethodRepository;

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id).orElse(null);
    }

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
        order.setOrderStatus(OrderStatus.PENDING); // Default order status to PENDING
        order = orderRepository.save(order);

        double totalAmount = 0;

        for (CartItem item : cartItems) {
            OrderDetail detail = new OrderDetail();
            detail.setOrder(order);
            detail.setProduct(item.getProduct());
            detail.setQuantity(item.getQuantity());
            orderDetailRepository.save(detail);

            double itemTotal = item.getProduct().getPrice() * item.getQuantity();
            totalAmount += itemTotal;
        }

        order.setTotalAmount(totalAmount); // Set total amount to order
        orderRepository.save(order); // Save order with total amount

        cartService.clearCart(); // Clear the cart after creating the order

        return order;
    }

    @Transactional
    public Order updateOrderStatus(Long orderId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid order Id:" + orderId));
        order.setOrderStatus(newStatus); // Update the order status
        return orderRepository.save(order);
    }

    @Transactional
    public void deleteOrder(Long id) {
        orderRepository.deleteById(id);
    }

    public List<Order> getOrdersByPhoneNumber(String phoneNumber) {
        return orderRepository.findByPhoneNumber(phoneNumber);
    }
    public List<Order> findOrdersByUserId(Long userId) {
        // Assuming there's a relationship between Order and User entities
        return orderRepository.findByUser_Id(userId);
    }
}