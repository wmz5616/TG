package com.example.messageservice.controller;

import com.example.messageservice.dto.ReadReceiptPayload;
import com.example.messageservice.model.Message;
import com.example.messageservice.service.MessageService;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import java.util.Map;

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
    @MessageMapping("/typing/{chatId}")
    public void handleTyping(
            @DestinationVariable String chatId,
            @Payload Map<String, Object> typingInfo // 直接接收 Map 对象
    ) {
        // 后端不再需要自己构建 Map，直接将收到的信息广播出去
        messagingTemplate.convertAndSend("/topic/typing/" + chatId, typingInfo);
    }
}
