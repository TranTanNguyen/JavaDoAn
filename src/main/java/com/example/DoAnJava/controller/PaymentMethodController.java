package com.example.DoAnJava.controller;

import com.example.DoAnJava.entity.CartItem;
import com.example.DoAnJava.entity.PaymentMethod;
import com.example.DoAnJava.services.CartService;
import com.example.DoAnJava.services.OrderService;
import com.example.DoAnJava.services.PaymentMethodService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@Controller
@RequestMapping("/payment-methods")
public class PaymentMethodController {
    @Autowired
    private PaymentMethodService paymentMethodService;

    @GetMapping
    public String showPaymentMethods(Model model) {
        model.addAttribute("paymentMethods", paymentMethodService.getAllPaymentMethods());
        return "payment-methods/list";
    }

    @GetMapping("/add")
    public String showAddPaymentMethodForm(Model model) {
        model.addAttribute("paymentMethod", new PaymentMethod());
        return "payment-methods/add";
    }

    @PostMapping("/add")
    public String addPaymentMethod(@Valid @ModelAttribute PaymentMethod paymentMethod, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "payment-methods/add";
        }
        paymentMethodService.savePaymentMethod(paymentMethod);
        return "redirect:/payment-methods";
    }

    @GetMapping("/edit/{id}")
    public String showEditPaymentMethodForm(@PathVariable Long id, Model model) {
        PaymentMethod paymentMethod = paymentMethodService.getPaymentMethodById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid payment method Id:" + id));
        model.addAttribute("paymentMethod", paymentMethod);
        return "payment-methods/edit";
    }

    @PostMapping("/edit/{id}")
    public String updatePaymentMethod(@PathVariable Long id, @Valid @ModelAttribute PaymentMethod paymentMethod, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "payment-methods/edit";
        }
        paymentMethod.setId(id);
        paymentMethodService.savePaymentMethod(paymentMethod);
        return "redirect:/payment-methods";
    }

    @GetMapping("/delete/{id}")
    public String deletePaymentMethod(@PathVariable Long id) {
        paymentMethodService.deletePaymentMethod(id);
        return "redirect:/payment-methods";
    }
}