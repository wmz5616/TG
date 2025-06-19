package com.example.userservice.dto;

import lombok.Data;

@Data
public class ProfileUpdateRequest {
    private String username;
    private String avatarUrl;
    private String bio;
}
