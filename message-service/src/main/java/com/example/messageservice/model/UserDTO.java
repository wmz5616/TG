package com.example.messageservice.model;

import lombok.Data;

// 这只是一个简单的数据容器，不包含任何数据库注解
@Data
public class UserDTO {
    private Long id;
    private String username;
    private String avatarUrl;
    private String bio;
    private String customId;
}
