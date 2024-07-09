package com.example.DoAnJava.controller;

import com.example.DoAnJava.entity.Order;
import com.example.DoAnJava.entity.User;
import com.example.DoAnJava.services.OrderService;
import com.example.DoAnJava.services.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller // Đánh dấu lớp này là một Controller trong Spring MVC.
@RequestMapping("/")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    @Autowired
    private OrderService orderService;
    @GetMapping("/login")
    public String login() {
        return "users/login";
    }
    @GetMapping("/register")
    public String register(@NotNull Model model) {
        model.addAttribute("user", new User()); // Thêm một đối tượng User mới vào model
        return "users/register";
    }
    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("user") User user, // Validateđối tượng User
                           @NotNull BindingResult bindingResult, // Kết quả của quátrình validate
                           Model model) {
        if (bindingResult.hasErrors()) { // Kiểm tra nếu có lỗi validate
            var errors = bindingResult.getAllErrors()
                    .stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toArray(String[]::new);
            model.addAttribute("errors", errors);
            return "users/register"; // Trả về lại view "register" nếu có lỗi
        }
        userService.save(user); // Lưu người dùng vào cơ sở dữ liệu
        userService.setDefaultRole(user.getUsername()); // Gán vai trò mặc định chongười dùng
        return "redirect:/login"; // Chuyển hướng người dùng tới trang "login"
    }

    @GetMapping("/recover-password")
    public String showRecoveryForm() {
        return "users/recover-password";
    }

    @PostMapping("/recover-password")
    public String handlePasswordRecovery(@RequestParam("email") String email, Model model) {
        try {
            if (userService.isEmailRegistered(email)) {
                String token = userService.createPasswordResetToken(email);
                userService.sendPasswordResetEmail(email, token);
                model.addAttribute("success", true);
                return "redirect:/login"; // Chuyển hướng đến trang recover-password
            } else {
                model.addAttribute("error", "Email không tồn tại trong hệ thống.");
            }
        } catch (Exception e) {
            model.addAttribute("error", "Đã có lỗi xảy ra, vui lòng thử lại.");
        }
        return "users/recover-password";
    }

    @GetMapping("/reset-password")
    public String showResetForm(@RequestParam("token") String token, Model model) {
        String email = userService.getEmailByToken(token);
        if (email == null) {
            model.addAttribute("error", "Token không hợp lệ hoặc đã hết hạn.");
            return "users/login";  // Hoặc trang báo lỗi khác
        }
        model.addAttribute("token", token);
        return "users/reset-password";
    }

    @PostMapping("/reset-password")
    public String handlePasswordReset(@RequestParam("token") String token, @RequestParam("password") String password, Model model) {
        String email = userService.getEmailByToken(token);
        if (email == null) {
            model.addAttribute("error", "Token không hợp lệ hoặc đã hết hạn.");
            return "users/login";  // Hoặc trang báo lỗi khác
        }
        userService.updatePassword(email, new BCryptPasswordEncoder().encode(password));
        model.addAttribute("resetSuccess", true);
        return "redirect:/login";
    }

}
