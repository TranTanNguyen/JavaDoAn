package com.example.DoAnJava.entity;

public class CartItem {
    private Product product;
    private int quantity;

    // Constructors
    public CartItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    // Getters and Setters
    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    // Method to calculate total price for this CartItem
    public double getTotalPrice() {
        return product.getPrice() * quantity;
    }

    // Overriding toString method to provide a string representation of CartItem
    @Override
    public String toString() {
        return "Sản phẩm: " + product.getName() + ", Số lượng: " + quantity + ", Giá: " + product.getPrice() + ", Tổng: " + getTotalPrice();
    }
}