package com.example.messageservice.dto;

import com.example.messageservice.model.MessageType;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用于向前端传递会话列表信息的DTO
 */
@Data
@NoArgsConstructor // Lombok注解，自动生成无参构造函数
public class ConversationDTO {

    private Long conversationId; // 会话ID (对于私聊是对方用户ID，对于群聊是群ID)
    private MessageType type;    // 会话类型 (PRIVATE / GROUP)
    private String name;         // 会话名称 (对方用户名或群名称)
    private String avatarUrl;    // 会话头像 (对方用户头像或群头像)

    // --- 核心新字段 ---
    private String lastMessageContent;     // 最后一条消息的内容
    private LocalDateTime lastMessageTimestamp; // 最后一条消息的时间
    private Long unreadCount;              // 未读消息数

    // 为了方便在Service层构建这个对象，我们创建一个全参构造函数
    public ConversationDTO(Long conversationId, MessageType type, String name, String avatarUrl, String lastMessageContent, LocalDateTime lastMessageTimestamp, Long unreadCount) {
        this.conversationId = conversationId;
        this.type = type;
        this.name = name;
        this.avatarUrl = avatarUrl;
        this.lastMessageContent = lastMessageContent;
        this.lastMessageTimestamp = lastMessageTimestamp;
        this.unreadCount = unreadCount;
    }
}
