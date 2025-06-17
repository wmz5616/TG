package com.example.messageservice.repository;

import com.example.messageservice.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    // 我们删掉了那个超长的方法名，用 @Query 注解来代替
    // 这段 JPQL 查询语句的含义和我们之前想要的是完全一致的，但更清晰、更不容易出错
    @Query("SELECT m FROM Message m WHERE (m.senderId = :user1Id AND m.recipientId = :user2Id) OR (m.senderId = :user2Id AND m.recipientId = :user1Id) ORDER BY m.timestamp ASC")
    List<Message> findChatHistory(@Param("user1Id") Long user1Id, @Param("user2Id") Long user2Id);
    List<Message> findByGroupIdOrderByTimestampAsc(Long groupId);

    // --- 在 MessageRepository.java 中添加 ---
    @Query("SELECT m.senderId FROM Message m WHERE m.recipientId = :userId AND m.messageType = 'PRIVATE' " +
            "UNION " +
            "SELECT m.recipientId FROM Message m WHERE m.senderId = :userId AND m.messageType = 'PRIVATE'")
    List<Long> findPrivateConversationPartnerIds(@Param("userId") Long userId);
}
