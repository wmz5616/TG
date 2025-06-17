package com.example.messageservice.controller;

import com.example.messageservice.dto.ReadReceiptPayload;
import com.example.messageservice.model.Message;
import com.example.messageservice.service.MessageService;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.Map; // 【新增】导入Map

@Controller
public class ChatController {

    private final MessageService messageService;
    private final SimpMessagingTemplate messagingTemplate;

    public ChatController(MessageService messageService, SimpMessagingTemplate messagingTemplate) {
        this.messageService = messageService;
        this.messagingTemplate = messagingTemplate;
    }

    // 处理新聊天消息 (保持不变)
    @MessageMapping("/chat")
    public void processMessage(@Payload Message message) {
            messageService.saveAndBroadcastMessage(message);
    }

    // 处理“消息已读”回执 (保持不变)
    @MessageMapping("/messages.read")
    public void handleReadReceipt(@Payload ReadReceiptPayload payload) {
        messageService.markMessagesAsRead(payload);
    }

    // 【【关键修正】】
    // 处理“正在输入”事件
    @MessageMapping("/typing/{chatId}")
    public void handleTyping(
            @DestinationVariable String chatId,
            @Payload String username // 前端仍然传来一个简单的用户名字符串
    ) {
        // 创建一个 Map 对象，它会被自动序列化为 JSON
        // 例如：{"username":"testuser1", "isTyping":true}
        Map<String, Object> typingInfo = Map.of(
                "username", username,
                "isTyping", true // 附带一个状态，方便前端处理
        );

        // 将这个 JSON 对象广播给对应聊天室的 topic
        messagingTemplate.convertAndSend("/topic/typing/" + chatId, typingInfo);
    }
}
