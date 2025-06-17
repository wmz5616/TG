package com.example.messageservice.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable; // <-- 1. 导入这个包
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "messages")
// 2. 实现 Serializable 接口
public class Message implements Serializable {

    // ... 类的其他内容保持不变 ...
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MessageType messageType;

    private Long senderId;
    private Long recipientId;
    private Long groupId;

    @Column(columnDefinition = "TEXT")
    private String content;

    @CreationTimestamp
    private LocalDateTime timestamp;

    // 【【新增字段】】
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MessageStatus status = MessageStatus.SENT; // 默认状态为“已发送”
}


