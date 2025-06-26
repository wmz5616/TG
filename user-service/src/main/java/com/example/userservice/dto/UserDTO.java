package com.example.userservice.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

// 这个DTO是专门用于微服务之间通信的，只包含公开和必要的信息
@Data
@NoArgsConstructor
public class UserDTO {
    private Long id;
    private String username;
    private String avatarUrl;
    private String customId;
}
