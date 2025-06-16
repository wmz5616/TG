package com.example.messageservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// "group-service" 是我们要调用的目标服务的名字，必须和它在 Nacos 中注册的名字完全一样
@FeignClient("group-service")
public interface GroupClient {

    // 这个方法声明，必须和 group-service 中 GroupController 里的方法签名和路径完全一致
    @GetMapping("/api/groups/{groupId}/members/{userId}/exists")
    boolean isUserMember(@PathVariable("groupId") Long groupId, @PathVariable("userId") Long userId);

}
