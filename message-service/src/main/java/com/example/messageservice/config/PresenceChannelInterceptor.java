package com.example.messageservice.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;
import java.util.Objects;

@Component
@Slf4j
public class PresenceChannelInterceptor implements ChannelInterceptor {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        StompCommand command = accessor.getCommand();

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
            }
        }
        return message;
    }
}
