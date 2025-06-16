package com.example.messageservice.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
<<<<<<< HEAD

import java.io.Serializable; // <-- 1. 导入这个包
=======
>>>>>>> 1a87df0d7045169a8a3e9611973c7c556173448b
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "messages")
<<<<<<< HEAD
// 2. 实现 Serializable 接口
public class Message implements Serializable {

    // ... 类的其他内容保持不变 ...
=======
public class Message {

>>>>>>> 1a87df0d7045169a8a3e9611973c7c556173448b
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

<<<<<<< HEAD
    @Enumerated(EnumType.STRING)
=======
    // --- 新增字段 ---
    @Enumerated(EnumType.STRING) // 告诉 JPA 在数据库中以字符串形式存储枚举值 (如 "PRIVATE", "GROUP")
>>>>>>> 1a87df0d7045169a8a3e9611973c7c556173448b
    @Column(nullable = false)
    private MessageType messageType;

    private Long senderId;
<<<<<<< HEAD
    private Long recipientId;
    private Long groupId;
=======

    // recipientId 对于群聊消息可以是 null
    private Long recipientId;

    // groupId 对于私聊消息可以是 null
    private Long groupId;
    // --- 新增结束 ---
>>>>>>> 1a87df0d7045169a8a3e9611973c7c556173448b

    @Column(columnDefinition = "TEXT")
    private String content;

    @CreationTimestamp
    private LocalDateTime timestamp;
<<<<<<< HEAD

    // 【【新增字段】】
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MessageStatus status = MessageStatus.SENT; // 默认状态为“已发送”
}


=======
}
>>>>>>> 1a87df0d7045169a8a3e9611973c7c556173448b
