package com.example.messageservice.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class FeignClientInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            String authorizationHeader = request.getHeader("Authorization");

            // 如果原始请求头中存在 "Authorization" (JWT)，则将其转发到 Feign 请求
            if (authorizationHeader != null) {
                template.header("Authorization", authorizationHeader);
            }
        }
    }
}
