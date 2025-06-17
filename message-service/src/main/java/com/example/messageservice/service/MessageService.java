package com.example.messageservice.service;

import com.example.messageservice.dto.ReadReceiptPayload;
import com.example.messageservice.model.Message;
import java.util.List;

/**
 * 消息服务的接口
 * 定义了所有消息相关的业务逻辑操作
 */
public interface MessageService {

    /**
     * 发送一条新消息
     * @param message 包含发送者、接收者和内容的消息对象
     * @return 保存到数据库后的完整消息对象
     */
    Message sendMessage(Message message);

    /**
     * 根据两个用户ID查找私聊历史记录
     * @param user1Id 用户1的ID
     * @param user2Id 用户2的ID
     * @return 消息列表
     */
    List<Message> getChatHistory(Long user1Id, Long user2Id);

    /**
     * 【新增的方法声明】
     * 根据群组ID查找群聊历史记录
     * @param groupId 群组的ID
     * @return 消息列表
     */
    List<Message> getGroupChatHistory(Long groupId);

    // --- 在 MessageService.java 中添加 ---
    List<Long> getPrivateConversationPartnerIds(Long userId);

    // 【新增】处理并发送消息的完整流程
    void processAndSendMessage(Message message);

    // 【新增】处理已读回执的逻辑
    void markMessagesAsRead(ReadReceiptPayload payload);

    Message saveAndBroadcastMessage(Message message);
}
