package com.example.DoAnJava.repository;

import com.example.DoAnJava.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IUserRepository extends JpaRepository<User, String> {
    Optional<User> findByUsername(String username);
    @Query("SELECT u FROM User u WHERE u.email = ?1")
    User findByEmail(String email);
}
