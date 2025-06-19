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
        // 【【修改】】检查 customId 是否已存在，并使用正确的参数名 registrationRequest
        if (userRepository.findByCustomId(registrationRequest.getCustomId()).isPresent()) {
            throw new UsernameAlreadyExistsException("Error: ID '" + registrationRequest.getCustomId() + "' is already taken!");
        }
        User newUser = new User();
        // 【【修改】】统一使用 registrationRequest
        newUser.setUsername(registrationRequest.getUsername());
        newUser.setCustomId(registrationRequest.getCustomId());
        newUser.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));
        newUser.setEmail(registrationRequest.getEmail());
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
        if (request.getUsername() != null && !request.getUsername().isEmpty()) {
            user.setUsername(request.getUsername());
        }

        if (request.getAvatarUrl() != null && !request.getAvatarUrl().isEmpty()) {
            user.setAvatarUrl(request.getAvatarUrl());
        }

        if (request.getBio() != null) {
            user.setBio(request.getBio());
        }

        User updatedUser = userRepository.save(user);

        // 将更新事件发送到RabbitMQ (此部分逻辑不变)
        rabbitTemplate.convertAndSend("im-exchange", "user.profile.updated", updatedUser);
        System.out.println("【事件发送】用户资料已更新，发送通知: " + updatedUser.getUsername());

        return updatedUser;
    }
}
