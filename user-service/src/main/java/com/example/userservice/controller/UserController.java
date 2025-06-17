package com.example.userservice.controller;

import com.example.userservice.model.User;
import com.example.userservice.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.userservice.dto.ProfileUpdateRequest; // 导入

import java.util.Collections;
import java.util.List;

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

        // 2. 【【授权核心】】检查数据库中用户的名字，是否和当前通过JWT认证的用户名一致
        if (!userInDb.getUsername().equals(authenticatedUsername)) {
            // 如果不一致，说明用户在尝试访问别人的信息，返回 403 Forbidden (禁止访问)
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // 3. 如果一致，说明是用户本人在操作，返回 200 OK 和用户信息
        return ResponseEntity.ok(userInDb);
    }

    // 这个批量查询接口我们暂时保留，因为聊天应用通常需要查询其他用户信息
    @GetMapping("/batch")
    public List<User> getUsersByIds(@RequestParam List<Long> ids) {
        return userService.getUsersByIds(ids);
    }

    // 【【核心修改】】
// 1. 路径从 "/{id}/profile" 改为 "/profile"
// 2. 方法参数不再接收 @PathVariable，而是直接从 @RequestHeader 获取用户ID
    @PutMapping("/profile")
    public ResponseEntity<?> updateUserProfile(
            @RequestHeader("X-Authenticated-User-Id") Long authenticatedUserId,
            @RequestBody ProfileUpdateRequest request) {

        // 不再需要比较ID，因为我们只信任从header中获取的authenticatedUserId
        // 这个ID是由API网关从JWT中解析后安全注入的，客户端无法伪造
        User updatedUser = userService.updateUserProfile(authenticatedUserId, request);
        return ResponseEntity.ok(updatedUser);
    }
}
