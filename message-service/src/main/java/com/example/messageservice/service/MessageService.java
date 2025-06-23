package com.example.messageservice.service;

import com.example.messageservice.dto.ConversationDTO;
import com.example.messageservice.dto.ReadReceiptPayload;
import com.example.messageservice.model.Message;
import java.util.List;

/**
 * 消息服务的接口
 * 定义了所有消息相关的业务逻辑操作
 */
public interface MessageService {
    Message sendMessage(Message message);
    List<Message> getChatHistory(Long user1Id, Long user2Id);
    List<Message> getGroupChatHistory(Long groupId);
    List<Long> getPrivateConversationPartnerIds(Long userId);
    void processAndSendMessage(Message message);
    void markMessagesAsRead(ReadReceiptPayload payload);
    Message saveAndBroadcastMessage(Message message);
    List<ConversationDTO> getConversations(Long userId);
}
