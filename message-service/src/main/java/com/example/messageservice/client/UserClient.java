package com.example.messageservice.client;

import com.example.messageservice.model.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;

// 1. @FeignClient 注解是关键！
//    "user-service" 是我们要调用的目标服务的名字，必须和它在 Nacos 中注册的名字完全一样。
@FeignClient("user-service")
public interface UserClient {

    // 2. 这里的方法声明，必须和 user-service 中 UserController 里的方法“签名”保持一致。
    //    我们声明我们要调用一个 GET 请求，路径是 /api/users/{id}
    @GetMapping("/api/users/{id}")
    Object getUserById(@PathVariable("id") Long id); // 3. 为了通用性，我们用 Object 接收返回结果

    @GetMapping("/api/users/batch")
    List<UserDTO> getUsersByIds(@RequestParam("ids") List<Long> ids);
}
