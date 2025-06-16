package com.example.userservice.model;

<<<<<<< HEAD
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "users")
public class User {
    // ... id, username, password, email 字段保持不变 ...
=======
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table; // <-- 需要手动或自动导入这个
import lombok.Data;

@Data
@Table(name = "users") // <-- 添加这一行！
@Entity
public class User {

>>>>>>> 1a87df0d7045169a8a3e9611973c7c556173448b
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

<<<<<<< HEAD
    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    private String email;

    // 【新增】头像URL字段
    private String avatarUrl;

    // 【新增】个人简介字段
    private String bio;
=======
    private String username;

    private String email;
>>>>>>> 1a87df0d7045169a8a3e9611973c7c556173448b
}
