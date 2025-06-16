package com.example.messageservice.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
<<<<<<< HEAD
=======
import org.springframework.context.annotation.Lazy;
>>>>>>> 1a87df0d7045169a8a3e9611973c7c556173448b
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;
<<<<<<< HEAD
import java.util.Objects;
=======

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
>>>>>>> 1a87df0d7045169a8a3e9611973c7c556173448b

@Component
@Slf4j
public class PresenceChannelInterceptor implements ChannelInterceptor {

    @Autowired
    private StringRedisTemplate redisTemplate;
<<<<<<< HEAD
=======
    private final Map<String, String> sessionUserMap = new ConcurrentHashMap<>();
>>>>>>> 1a87df0d7045169a8a3e9611973c7c556173448b

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        StompCommand command = accessor.getCommand();

<<<<<<< HEAD
        if (StompCommand.CONNECT.equals(command)) {
            String userId = accessor.getFirstNativeHeader("userId");
            String sessionId = accessor.getSessionId();

            log.info("STOMP Connect: 收到连接请求. UserId: [{}], SessionId: [{}]", userId, sessionId);

            if (userId != null && sessionId != null) {
                // 【【关键修正】】为这个连接设置用户凭证
                // 这一步是让 Spring 知道这个 session 属于谁，是 /user 点对点消息能工作的核心
                StompPrincipal principal = new StompPrincipal(userId);
                accessor.setUser(principal);

                // 将 userId 存入 session 属性，方便在断开连接时获取
                Objects.requireNonNull(accessor.getSessionAttributes()).put("userId", userId);

                // 更新 Redis 中的在线状态
                redisTemplate.opsForValue().set("user:status:" + userId, "online");
                log.info("用户 {} 上线成功. SessionId: {}", userId, sessionId);
            }
        } else if (StompCommand.DISCONNECT.equals(command)) {
            try {
                String userId = (String) Objects.requireNonNull(accessor.getSessionAttributes()).get("userId");
                if (userId != null) {
                    redisTemplate.delete("user:status:" + userId);
                    log.info("用户 {} 下线成功. SessionId: {}", userId, accessor.getSessionId());
                }
            } catch (Exception e) {
                log.error("处理DISCONNECT命令时发生错误!", e);
=======
        // 如果是连接命令
        if (StompCommand.CONNECT.equals(command)) {
            // 在这里处理上线逻辑
            Map<String, Object> sessionAttributes = accessor.getSessionAttributes();
            if (sessionAttributes != null) {
                String userId = (String) sessionAttributes.get("userId");
                String sessionId = accessor.getSessionId();
                if (userId != null && sessionId != null) {
                    sessionUserMap.put(sessionId, userId);
                    redisTemplate.opsForValue().set("user:status:" + userId, "online");
                    log.info("STOMP Connect: 用户 {} 上线了, SessionId: {}", userId, sessionId);
                }
            }
        }
        // 如果是断开连接命令
        else if (StompCommand.DISCONNECT.equals(command)) {
            // 在这里处理下线逻辑
            String sessionId = accessor.getSessionId();
            String userId = sessionUserMap.get(sessionId);
            if (userId != null) {
                redisTemplate.delete("user:status:" + userId);
                sessionUserMap.remove(sessionId);
                log.info("STOMP Disconnect: 用户 {} 下线了, SessionId: {}", userId, sessionId);
>>>>>>> 1a87df0d7045169a8a3e9611973c7c556173448b
            }
        }
        return message;
    }
}
