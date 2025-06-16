package com.example.userservice.dto;

import lombok.Data;

@Data
public class ProfileUpdateRequest {
    // 这两个字段都是可选的，前端可以只传一个
    private String avatarUrl;
    private String bio;
}
