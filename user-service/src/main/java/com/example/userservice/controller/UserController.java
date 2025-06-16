package com.example.userservice.controller;

import com.example.userservice.model.User;
<<<<<<< HEAD
import com.example.userservice.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.userservice.dto.ProfileUpdateRequest; // 导入

import java.util.Collections;
=======
import com.example.userservice.service.UserService; // **注意：这里变了**
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
>>>>>>> 1a87df0d7045169a8a3e9611973c7c556173448b
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

<<<<<<< HEAD
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

    // 我们将修改这个方法，让它打印出所有收到的请求头
    @PutMapping("/{id}/profile")
    public ResponseEntity<?> updateUserProfile(
            @PathVariable Long id,
            @RequestBody ProfileUpdateRequest request,
            HttpServletRequest httpServletRequest) { // 注入原始的HTTP请求对象

        // --- 【【诊断代码开始】】 ---
        System.out.println("--- [DEBUG] Received Headers in user-service ---");
        // 获取所有请求头的名字
        List<String> headerNames = Collections.list(httpServletRequest.getHeaderNames());
        for (String headerName : headerNames) {
            // 打印每个请求头的名字和值
            System.out.println(headerName + ": " + httpServletRequest.getHeader(headerName));
        }
        System.out.println("----------------------------------------------");
        // --- 【【诊断代码结束】】 ---

        // 现在我们尝试从请求中手动获取header
        String authenticatedUserIdStr = httpServletRequest.getHeader("X-Authenticated-User-Id");

        // 如果头信息不存在，我们返回一个特殊的418状态码，方便快速定位问题
        if (authenticatedUserIdStr == null) {
            System.err.println("!!! ERROR: Header 'X-Authenticated-User-Id' is NULL!");
            return ResponseEntity.status(HttpStatus.I_AM_A_TEAPOT).body("Missing X-Authenticated-User-Id header");
        }

        Long authenticatedUserId = Long.valueOf(authenticatedUserIdStr);
        if (!id.equals(authenticatedUserId)) {
            System.err.println("!!! ERROR: Forbidden access. Path ID: " + id + ", Authenticated ID: " + authenticatedUserId);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        User updatedUser = userService.updateUserProfile(id, request);
        return ResponseEntity.ok(updatedUser);
=======
    // 1. 不再注入 UserRepository，而是注入我们新的 UserService
    @Autowired
    private UserService userService;

    @GetMapping
    public List<User> getAllUsers() {
        // 2. 调用 service 层的方法，而不是 repository 的
        return userService.getAllUsers();
    }

    @PostMapping
    public User createUser(@RequestBody User user) {
        // 3. 调用 service 层的方法
        return userService.createUser(user);
    }

    /**
     * API 3: 根据用户ID获取单个用户信息
     * 请求方式: GET
     * 请求地址: http://localhost:8080/api/users/{id}  (例如 /api/users/1)
     */
    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        // @PathVariable 会将URL路径中的 {id} 值赋给方法的 id 参数
        return userService.getUserById(id);
>>>>>>> 1a87df0d7045169a8a3e9611973c7c556173448b
    }
}
