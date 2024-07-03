package com.example.DoAnJava.controller;

import com.example.DoAnJava.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class HomeController {
    @Autowired
    private ProductService productService;
    @GetMapping
    public String index(Model model) {
        model.addAttribute("products", productService.getAllProducts());
        return "home/index";
    }
}
