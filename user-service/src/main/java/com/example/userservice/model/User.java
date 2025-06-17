package com.example.userservice.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "users")
public class User {
    // ... id, username, password, email 字段保持不变 ...
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    private String email;

    // 【新增】头像URL字段
    private String avatarUrl;

    // 【新增】个人简介字段
    private String bio;
}
