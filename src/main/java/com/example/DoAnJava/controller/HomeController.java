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
    @GetMapping("/gioi-thieu") // Đường dẫn đến trang giới thiệu
    public String about() {
        return "home/about"; // Trả về trang giới thiệu (about.html)
    }

    @GetMapping("/chinh-sach-bao-hanh") // Đường dẫn đến trang chính sách bảo hành
    public String csbh() {
        return "home/csbh"; // Trả về trang chính sách bảo hành (csbh.html)
    }

    @GetMapping("/lien-he") // Đường dẫn đến trang liên hệ
    public String contact() {
        return "home/contact"; // Trả về trang liên hệ (contact.html)
    }
}
