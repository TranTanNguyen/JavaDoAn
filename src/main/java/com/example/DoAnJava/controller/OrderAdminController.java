package com.example.DoAnJava.controller;

import com.example.DoAnJava.entity.Order;
import com.example.DoAnJava.entity.OrderStatus;
import com.example.DoAnJava.services.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class OrderAdminController {
    private final OrderService orderService;
    private final OrderStatus[] orderStatusList = OrderStatus.values(); // Thêm danh sách trạng thái đơn hàng vào đây

    @GetMapping("/list")
    public String getAllOrders(Model model) {
        model.addAttribute("orders", orderService.getAllOrders());
        return "admin/order-list";
    }

    @GetMapping("/detail/{id}")
    public String getOrderById(@PathVariable Long id, Model model) {
        Order order = orderService.getOrderById(id);
        model.addAttribute("order", order);
        model.addAttribute("orderStatusList", orderStatusList); // Đưa danh sách trạng thái đơn hàng vào model
        return "admin/order-detail";
    }

    @PostMapping("/updateOrderStatus")
    public String updateOrderStatus(@RequestParam("orderId") Long orderId,
                                    @RequestParam("newStatus") OrderStatus newStatus) {
        orderService.updateOrderStatus(orderId, newStatus);
        return "redirect:/admin/list";
    }

    @GetMapping("/invoice/{id}")
    public String generateInvoice(@PathVariable Long id, Model model) {
        Order order = orderService.getOrderById(id);
        model.addAttribute("order", order);
        return "admin/invoice";
    }
}
