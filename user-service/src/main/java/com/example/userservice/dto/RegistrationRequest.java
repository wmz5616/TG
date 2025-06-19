package com.example.userservice.dto;

import lombok.AllArgsConstructor; // 【新增】导入这个包
import lombok.Data;
import lombok.NoArgsConstructor;   // 【新增】导入这个包

@Data
@NoArgsConstructor      // 【新增】这个注解会生成一个无参数的构造函数
@AllArgsConstructor // 【新增】这个注解会生成一个包含所有字段的构造函数
public class RegistrationRequest {
    private String username;
    private String customId;
    private String password;
    private String email;
}
