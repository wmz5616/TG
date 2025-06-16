package com.example.groupservice.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "group_members")
public class GroupMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 这条成员关系的唯一ID

    @Column(nullable = false)
    private Long groupId; // 对应的群组ID

    @Column(nullable = false)
    private Long userId; // 对应的用户ID

    @CreationTimestamp
    private LocalDateTime joinedAt; // 加入时间
}
