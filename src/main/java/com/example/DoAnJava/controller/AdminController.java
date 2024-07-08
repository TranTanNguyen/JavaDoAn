package com.example.DoAnJava.controller;

import com.example.DoAnJava.services.UserService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    public AdminController(UserService userService, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
    }

    @GetMapping("/users")
    public String listUsers(Model model) {
        model.addAttribute("users", userService.findAllUsers());
        return "admin/user-list";
    }

    @GetMapping("/edit/{id}")
    public String editUser(@PathVariable String username, Model model) {
        model.addAttribute("user", userService.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found")));
        return "admin/user-edit";
    }

    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable long id) {
        userService.deleteUserById(id);
        return "redirect:/admin/users";
    }

    @GetMapping("/toggleApproval/{id}")
    public String toggleApproval(@PathVariable Long id) {
        userService.toggleApproval(id);
        return "redirect:/admin/users";
    }

    @GetMapping("/toggleLock/{id}")
    public String toggleLock(@PathVariable Long id) {
        userService.toggleLock(id);
        return "redirect:/admin/users";
    }
}
