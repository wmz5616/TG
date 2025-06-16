package com.example.userservice.controller;

import com.example.userservice.dto.AuthenticationRequest;
import com.example.userservice.dto.AuthenticationResponse;
import com.example.userservice.dto.RegistrationRequest;
import com.example.userservice.model.User; // 【【修正】】添加 User 类的 import
import com.example.userservice.repository.UserRepository; // 【【修正】】添加 UserRepository 的 import
import com.example.userservice.service.UserService;
import com.example.userservice.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserService userService;

    // 【【修正】】注入 UserRepository，以便在登录成功后查询用户信息
    @Autowired
    private UserRepository userRepository;

    // 注册接口 (保持不变)
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegistrationRequest registrationRequest) {
        userService.registerNewUser(registrationRequest);
        return ResponseEntity.ok("User registered successfully");
    }

    // 登录接口 (修正)
    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest) throws Exception {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword())
            );
        } catch (Exception e) {
            throw new Exception("Incorrect username or password", e);
        }

        final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());

        // 从数据库中获取完整的 User 对象，以便拿到 ID
        final User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new Exception("User not found after authentication"));

        final String jwt = jwtUtil.generateToken(userDetails, user.getId());

        // 返回包含 JWT、userId 和 username 的完整响应
        return ResponseEntity.ok(new AuthenticationResponse(jwt, user.getId(), user.getUsername()));
    }
}
