package com.example.groupservice.client;

import com.example.groupservice.dto.GroupMemberDTO; // DTO for user data
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient("user-service") // Name of the user service in Nacos
public interface UserClient {
    // This signature must match a method in user-service's UserController
    @GetMapping("/api/users/batch")
    List<GroupMemberDTO> getUsersByIds(@RequestParam("ids") List<Long> ids);
}
