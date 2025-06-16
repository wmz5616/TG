package com.example.messageservice.controller;

import com.example.messageservice.model.Message;
import com.example.messageservice.service.MessageService;
<<<<<<< HEAD
import org.springframework.web.bind.annotation.*;
=======
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

>>>>>>> 1a87df0d7045169a8a3e9611973c7c556173448b
import java.util.List;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

<<<<<<< HEAD
    // 1. 我们不再使用 @Autowired 在这里
    private final MessageService messageService;

    // 2. 我们创建一个构造函数，并通过它来注入 MessageService
    // 这就是“构造函数注入”，是Spring推荐的最佳实践
    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping
    public Message sendMessage(@RequestBody Message message) {
        return messageService.sendMessage(message);
    }

    // 这是获取私聊历史的旧接口，保留
    @GetMapping("/history")
    public List<Message> getHistory(@RequestParam Long user1Id, @RequestParam Long user2Id) {
        return messageService.getChatHistory(user1Id, user2Id);
    }

    // 这是我们新添加的、用于获取群聊历史的接口
    @GetMapping("/group/{groupId}/history")
    public List<Message> getGroupHistory(@PathVariable Long groupId) {
        // Controller 只负责调用 service，将参数传给它，然后返回结果
        return messageService.getGroupChatHistory(groupId);
    }

    // --- 在 MessageController.java 中添加 ---
    @GetMapping("/conversations/private/{userId}")
    public List<Long> getPrivateConversations(@PathVariable Long userId) {
        return messageService.getPrivateConversationPartnerIds(userId);
    }
=======
    @Autowired
    private MessageService messageService;

    @PostMapping
    public Message sendMessage(@RequestBody Message message) {
        // Controller 的职责非常单纯：接收请求，把它完整地交给 Service 去处理，然后把处理结果返回。
        return messageService.sendMessage(message);
    }

    @GetMapping("/history")
    public List<Message> getHistory(@RequestParam Long user1Id, @RequestParam Long user2Id) {
        // @RequestParam 用于获取 URL 查询参数，例如 ?user1Id=1&user2Id=2
        return messageService.getChatHistory(user1Id, user2Id);
    }
>>>>>>> 1a87df0d7045169a8a3e9611973c7c556173448b
}
