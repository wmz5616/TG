package com.example.messageservice.controller;

import com.example.messageservice.model.Message;
import com.example.messageservice.service.MessageService;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import com.example.messageservice.dto.ConversationDTO;

@RestController
@RequestMapping("/api/messages")
public class MessageController {
    private final MessageService messageService;
    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping
    public Message sendMessage(@RequestBody Message message) {
        return messageService.sendMessage(message);
    }
    @GetMapping("/history")
    public List<Message> getHistory(@RequestParam Long user1Id, @RequestParam Long user2Id) {
        return messageService.getChatHistory(user1Id, user2Id);
    }
    @GetMapping("/group/{groupId}/history")
    public List<Message> getGroupHistory(@PathVariable Long groupId) {
        return messageService.getGroupChatHistory(groupId);
    }
    @GetMapping("/conversations/private/{userId}")
    public List<Long> getPrivateConversations(@PathVariable Long userId) {
        return messageService.getPrivateConversationPartnerIds(userId);
    }
    @GetMapping("/conversations")
    public List<ConversationDTO> getUserConversations(@RequestHeader("X-Authenticated-User-Id") Long userId) {
        return messageService.getConversations(userId);
    }
}
