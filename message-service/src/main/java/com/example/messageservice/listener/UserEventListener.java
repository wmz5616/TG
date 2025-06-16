package com.example.messageservice.listener;

import com.example.messageservice.model.UserDTO; // 【【关键修正】】导入我们新建的 UserDTO
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class UserEventListener {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    // 【【关键修正】】
    // 将监听方法参数的类型从 User 改为 UserDTO
    @RabbitListener(queues = "im-queue")
    public void handleUserProfileUpdate(UserDTO updatedUser) {
        // 检查一下，确保这不是一条普通的消息或错误的数据
        if (updatedUser != null && updatedUser.getId() != null) {
            System.out.println("【事件】收到用户资料更新事件: " + updatedUser.getUsername());

            // 将更新后的用户信息，广播到一个所有客户端都订阅的公共频道
            // 前端JS会接收到这个 UserDTO 对象
            messagingTemplate.convertAndSend("/topic/users/updated", updatedUser);
        }
    }
}
