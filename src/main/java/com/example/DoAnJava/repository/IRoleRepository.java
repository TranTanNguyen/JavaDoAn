package com.example.DoAnJava.repository;

import com.example.DoAnJava.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
public interface IRoleRepository extends JpaRepository<Role, Long> {
    Role findRoleById(Long id);
}
