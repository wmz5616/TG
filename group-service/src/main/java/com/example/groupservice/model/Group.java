package com.example.groupservice.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "chat_groups") // 用 chat_groups 作为表名，避免和SQL关键字 group 冲突
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 群组的唯一ID

    @Column(nullable = false)
    private String name; // 群组名称

    private String description; // 群组描述

    @Column(nullable = false)
    private Long ownerId; // 群主的 userId

    @CreationTimestamp
    private LocalDateTime createdAt; // 群组创建时间
}
