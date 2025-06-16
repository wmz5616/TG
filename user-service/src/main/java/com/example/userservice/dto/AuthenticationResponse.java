package com.example.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor      // 【新增】这个注解会生成一个无参数的构造函数
@AllArgsConstructor // 【新增】这个注解会生成一个包含所有字段的构造函数
public class AuthenticationResponse {

    // 【修改】去掉了 final，以配合 @NoArgsConstructor
    private String jwt;
    private Long userId;
    private String username;

}
