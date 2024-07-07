package com.example.DoAnJava.controller;

import com.example.DoAnJava.entity.Product;
import com.example.DoAnJava.services.CategoryService;
import com.example.DoAnJava.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Controller
@RequestMapping("/products")
public class ProductController {
    @Autowired
    private ProductService productService;
    private static final String UPLOADED_DIR = "src/main/resources/static/image/";
    @Autowired
    private CategoryService categoryService; // Đảm bảo bạn đã inject CategoryService
    // Display a list of all products
    @GetMapping
    public String showProductList(Model model) {
        model.addAttribute("products", productService.getAllProducts());
        return "/products/products-list";
    }
    // For adding a new product
    @GetMapping("/add")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String showAddForm(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("categories", categoryService.getAllCategories()); //Load categories
        return "/products/add-product";
    }
    // Process the form for adding a new product
    @PostMapping("/add")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String addProduct(@Valid Product product, @RequestParam("image") MultipartFile file, BindingResult result) {
        if (result.hasErrors()) {
            return "/products/add-product";
        }
        try {
            byte[] bytes = file.getBytes();
            Path path = Paths.get(UPLOADED_DIR+file.getOriginalFilename());
            Files.write(path,bytes);
            product.setImagePath("/image/" + file.getOriginalFilename());
        }catch (IOException e){
            e.printStackTrace();
        }
        productService.addProduct(product);
        return "redirect:/products";
    }
    // For editing a product
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Product product = productService.getProductById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid product Id:" + id));
        model.addAttribute("product", product);
        model.addAttribute("categories", categoryService.getAllCategories());
        return "/products/update-product";
    }
    // Process the form for updating a product
    @PostMapping("/update/{id}")
    public String updateProduct(@PathVariable Long id,@RequestParam("image") MultipartFile file, @Valid Product product,  BindingResult result) {
        if (result.hasErrors()) {
            product.setId(id);
            return "/products/update-product";
        }
        try {
            byte[] bytes = file.getBytes();
            Path path = Paths.get(UPLOADED_DIR+file.getOriginalFilename());
            Files.write(path,bytes);
            product.setImagePath("/image/" + file.getOriginalFilename());
        }catch (IOException e){
            e.printStackTrace();
        }
        productService.updateProduct(product);
        return "redirect:/products";
    }

    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long id) {
        productService.deleteProductById(id);
        return "redirect:/products";
    }

    @GetMapping("/search-product")
    public String searchPost(@RequestParam("productName") String productName, Model model) { // Thay đổi tham số từ "postTitle" thành "productName"
        List<Product> productList = productService.searchProducts(productName);

        if (productList.isEmpty()) {
            model.addAttribute("productNotFound", true);
            return "/products/not-found-product"; // Chuyển hướng về trang danh sách sản phẩm, không phải "/products-list"
        } else {
            model.addAttribute("products", productList); // Sửa "posts" thành "products"
            return "/products/product-search";
        }
    }

    @GetMapping("/{id}")
    public String getProductDetail(@PathVariable Long id, Model model) {
        Product product = productService.getProductById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid product Id:" + id));
        model.addAttribute("product", product);
        return "/products/product-detail";
    }


}
