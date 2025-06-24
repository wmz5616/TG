package com.example.messageservice.listener;

import com.example.messageservice.config.RabbitMQConfig;
import com.example.messageservice.model.UserDTO; // 【【关键修正】】导入我们新建的 UserDTO
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class UserEventListener {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @RabbitListener(queues = RabbitMQConfig.USER_EVENTS_QUEUE_NAME)
    public void handleUserProfileUpdate(UserDTO updatedUser) {
        if (updatedUser != null && updatedUser.getId() != null) {
            System.out.println("【事件】收到用户资料更新事件: " + updatedUser.getUsername());
            messagingTemplate.convertAndSendToUser(
                    updatedUser.getId().toString(),
                    "/queue/profile-updates",  // 使用私有队列
                    updatedUser
            );
        }
    }
}
