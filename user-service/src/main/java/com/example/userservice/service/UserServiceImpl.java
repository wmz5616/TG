package com.example.userservice.service;

import com.example.userservice.dto.ProfileUpdateRequest;
import com.example.userservice.dto.RegistrationRequest;
import com.example.userservice.exception.UsernameAlreadyExistsException;
import com.example.userservice.model.User;
import com.example.userservice.repository.UserRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // 【新增】注入RabbitTemplate，用于发送事件通知
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    public User registerNewUser(RegistrationRequest registrationRequest) {
        if (userRepository.findByUsername(registrationRequest.getUsername()).isPresent()) {
            throw new UsernameAlreadyExistsException("Error: Username '" + registrationRequest.getUsername() + "' is already taken!");
        }
        User newUser = new User();
        newUser.setUsername(registrationRequest.getUsername());
        newUser.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));
        newUser.setEmail(registrationRequest.getEmail());
        // 默认头像或简介可以在这里设置
        // newUser.setAvatarUrl("default_avatar.png");
        return userRepository.save(newUser);
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public List<User> getUsersByIds(List<Long> ids) {
        return userRepository.findAllById(ids);
    }

    @Override
    public List<User> searchUsersByUsername(String query) {
        return userRepository.findByUsernameContainingIgnoreCase(query);
    }

    // 【修改】为更新个人资料的方法添加事件广播
    @Override
    @Transactional
    public User updateUserProfile(Long userId, ProfileUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        // 如果请求中包含 avatarUrl，则更新
        if (request.getAvatarUrl() != null && !request.getAvatarUrl().isEmpty()) {
            user.setAvatarUrl(request.getAvatarUrl());
        }

        // 如果请求中包含 bio，则更新
        if (request.getBio() != null) {
            user.setBio(request.getBio());
        }

        User updatedUser = userRepository.save(user);

        // 【【关键新增】】
        // 将更新后的用户信息，作为一个事件，发送到RabbitMQ的交换机中
        // message-service 将会监听到这个事件，并把它广播给所有在线的客户端
        rabbitTemplate.convertAndSend("im-exchange", "user.profile.updated", updatedUser);
        System.out.println("【事件发送】用户资料已更新，发送通知: " + updatedUser.getUsername());

        return updatedUser;
    }
}
