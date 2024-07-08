package com.example.DoAnJava.services;

import com.example.DoAnJava.entity.User;
import com.example.DoAnJava.repository.IRoleRepository;
import com.example.DoAnJava.repository.IUserRepository;
import com.example.DoAnJava.utils.Role;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Slf4j
@Transactional
public class UserService implements UserDetailsService {

    @Autowired
    private IUserRepository userRepository;
    @Autowired
    private IRoleRepository roleRepository;
    @Autowired
    private EmailServices emailServices;

    private Map<String, String> tokens = new HashMap<>();

    // Save new user to the database after encrypting password.
    public void save(@NotNull User user) {
        user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
        userRepository.save(user);
    }

    // Set default role for a user based on username.
    public void setDefaultRole(String username) {
        userRepository.findByUsername(username).ifPresentOrElse(user -> {
            user.getRoles().add(roleRepository.findRoleById(Role.USER.value));
            userRepository.save(user);
        }, () -> {
            throw new UsernameNotFoundException("User not found");
        });
    }

    // Load user details for authentication.
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .authorities(user.getAuthorities())
                .accountExpired(!user.isAccountNonExpired())
                .accountLocked(!user.isAccountNonLocked()) // Invert account locked status for Spring Security
                .credentialsExpired(!user.isCredentialsNonExpired())
                .disabled(!user.isEnabled())
                .build();
    }

    // Find user by username.
    public Optional<User> findByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username);
    }

    // Check if email is already registered.
    public boolean isEmailRegistered(String email) {
        return userRepository.findByEmail(email) != null;
    }

    // Create password reset token and store in tokens map.
    public String createPasswordResetToken(String email) {
        String token = UUID.randomUUID().toString();
        tokens.put(token, email);
        return token;
    }

    // Retrieve email by token from tokens map.
    public String getEmailByToken(String token) {
        return tokens.get(token);
    }

    // Send password reset email with token link.
    public void sendPasswordResetEmail(String to, String token) {
        String subject = "Password Reset";
        String text = "Click the following link to reset your password: " + "http://localhost:9999/reset-password?token=" + token;
        emailServices.sendEmail(to, subject, text);
    }

    // Update user password after finding by email.
    @Transactional
    public void updatePassword(String email, String newPassword) {
        User user = userRepository.findByEmail(email);
        if (user != null) {
            user.setPassword(new BCryptPasswordEncoder().encode(newPassword));
            userRepository.save(user);
            System.out.println("Password updated successfully for email: " + email);
        } else {
            throw new IllegalArgumentException("User with provided email not found");
        }
    }

    // Retrieve all users.
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    // Find user by ID.
    public User findUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    // Delete user by ID.
    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }

    // Toggle user approval status.
    public void toggleApproval(Long id) {
        User user = findUserById(id);
        user.setApproved(!user.isApproved());
        userRepository.save(user);
    }

    @Transactional
    public void toggleLock(Long id) {
        User user = findUserById(id);
        boolean isLocked = !user.isAccountNonLocked();

        user.setAccountNonLocked(isLocked);
        userRepository.save(user);

        // If the user is being locked, log them out
        if (!user.isAccountNonLocked()) {
            SecurityContextHolder.getContext().setAuthentication(null);
        }
    }

}
