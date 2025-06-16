package com.example.userservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

//@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 这段代码和另外两个服务里的完全一样
        registry.addMapping("/**")       // 允许所有路径
                .allowedOriginPatterns("*") // 允许所有来源的请求
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 允许的方法
                .allowCredentials(true)       // 允许携带凭证
                .maxAge(3600);                // 预检请求的有效期
    }
}
