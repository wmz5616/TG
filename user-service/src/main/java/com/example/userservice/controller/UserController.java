package com.example.userservice.controller;

import com.example.userservice.model.User;
import com.example.userservice.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.userservice.dto.ProfileUpdateRequest;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.Collections;
import java.util.List;
import com.example.userservice.dto.UserDTO;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    // 【【新增】】用户搜索接口
    @GetMapping("/search")
    public List<User> searchUsers(@RequestParam String query) {
        // 为了安全，可以限制返回结果的数量和过滤掉敏感信息
        return userService.searchUsersByUsername(query);
    }
    // 【【授权改造】】
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(
            @PathVariable Long id,
            // 1. 使用 @RequestHeader注解，从请求头中获取由网关放入的用户名
            @RequestHeader("X-Authenticated-Username") String authenticatedUsername) {
        User userInDb = userService.getUserById(id);
        // 如果要查询的用户不存在，返回 404
        if (userInDb == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(userInDb);
    }

    @GetMapping("/batch")
    public List<UserDTO> getUsersByIds(@RequestParam List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }
        List<User> users = userService.getUsersByIds(ids);
        return users.stream().map(user -> {
            UserDTO dto = new UserDTO();
            dto.setId(user.getId());
            dto.setUsername(user.getUsername());
            dto.setAvatarUrl(user.getAvatarUrl());
            dto.setCustomId(user.getCustomId()); // 【【新增】】 填充 customId
            return dto;
        }).collect(Collectors.toList());
    }
    @PutMapping("/profile")
    public ResponseEntity<?> updateUserProfile(
            @RequestHeader("X-Authenticated-User-Id") Long authenticatedUserId,
            @RequestBody ProfileUpdateRequest request) {
        User updatedUser = userService.updateUserProfile(authenticatedUserId, request);
        return ResponseEntity.ok(updatedUser);
    }
}
