package com.example.userservice.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 【【修改1】】移除 unique = true 约束，用户名现在可以重复
    @Column(nullable = false)
    private String username;

    // 【【新增】】添加唯一的、用户可自定义的ID号
    @Column(nullable = false, unique = true)
    private String customId;

    @Column(nullable = false)
    private String password;

    private String email;

    // 【新增】头像URL字段
    private String avatarUrl;

    // 【新增】个人简介字段
    private String bio;
}
