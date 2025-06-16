package com.example.messageservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

<<<<<<< HEAD
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

=======
>>>>>>> 1a87df0d7045169a8a3e9611973c7c556173448b
@Service
public class PresenceServiceImpl implements PresenceService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public String getUserStatus(Long userId) {
        String status = redisTemplate.opsForValue().get("user:status:" + userId);
        // 如果能从 Redis 拿到 "online" 值，就返回 "Online"，否则都算 "Offline"
        return "online".equals(status) ? "Online" : "Offline";
    }
<<<<<<< HEAD

    // 2. 实现这个新方法
    @Override
    public Map<Long, String> getUsersStatus(List<Long> userIds) {
        return userIds.stream()
                .collect(Collectors.toMap(
                        userId -> userId, // Map 的 Key 就是 userId
                        this::getUserStatus   // Map 的 Value 就是调用已有的方法得到的状态
                ));
    }
=======
>>>>>>> 1a87df0d7045169a8a3e9611973c7c556173448b
}
