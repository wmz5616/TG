package com.example.userservice.service;

import com.example.userservice.dto.RegistrationRequest; // 导入我们之前创建的DTO
import com.example.userservice.model.User;
import java.util.List;
import com.example.userservice.dto.ProfileUpdateRequest;

public interface UserService {

    /**
     * 【修改】注册一个新用户，使用 DTO 来接收参数，更安全
     * @param registrationRequest 包含用户名、密码等信息的注册请求
     * @return 创建成功后的用户对象
     */
    User registerNewUser(RegistrationRequest registrationRequest);

    /**
     * 【保留】根据ID查找用户
     * @param id 用户ID
     * @return 找到的用户
     */
    User getUserById(Long id);

    /**
     * 【保留】根据ID列表批量查找用户
     * @param ids 用户ID列表
     * @return 用户列表
     */
    List<User> getUsersByIds(List<Long> ids);

    List<User> searchUsersByUsername(String query);
    // 【新增】更新用户个人资料的方法
    User updateUserProfile(Long userId, ProfileUpdateRequest request);
}
