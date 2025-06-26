package com.example.messageservice.repository;

import com.example.messageservice.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Map;

public interface MessageRepository extends JpaRepository<Message, Long> {

    @Query("SELECT m FROM Message m WHERE (m.senderId = :user1Id AND m.recipientId = :user2Id) OR (m.senderId = :user2Id AND m.recipientId = :user1Id) ORDER BY m.timestamp ASC")
    List<Message> findChatHistory(@Param("user1Id") Long user1Id, @Param("user2Id") Long user2Id);
    List<Message> findByGroupIdOrderByTimestampAsc(Long groupId);
    @Query("SELECT m.senderId FROM Message m WHERE m.recipientId = :userId AND m.messageType = 'PRIVATE' " +
            "UNION " +
            "SELECT m.recipientId FROM Message m WHERE m.senderId = :userId AND m.messageType = 'PRIVATE'")
    List<Long> findPrivateConversationPartnerIds(@Param("userId") Long userId);
    @Query(value = """
    WITH ranked_messages AS (
        SELECT *,
               ROW_NUMBER() OVER(PARTITION BY
                   CASE
                       WHEN message_type = 'PRIVATE' THEN IF(sender_id = :userId, recipient_id, sender_id)
                       ELSE group_id
                   END
                   ORDER BY timestamp DESC
               ) as rn
        FROM messages
        WHERE (message_type = 'PRIVATE' AND (sender_id = :userId OR recipient_id = :userId))
           OR (message_type = 'GROUP' AND group_id IN (:groupIds))
    ),
    unread_counts AS (
        SELECT
            CASE
                WHEN message_type = 'PRIVATE' THEN sender_id
                ELSE group_id
            END as conversation_id,
            COUNT(*) as unread_count
        FROM messages
        WHERE recipient_id = :userId AND status <> 'READ'
        GROUP BY conversation_id
    )
    SELECT
        CASE
            WHEN rm.message_type = 'PRIVATE' THEN IF(rm.sender_id = :userId, rm.recipient_id, rm.sender_id)
            ELSE rm.group_id
        END AS conversationId,
        rm.message_type AS type,
        rm.content AS lastMessageContent,
        rm.timestamp AS lastMessageTimestamp,
        COALESCE(uc.unread_count, 0) AS unreadCount
    FROM ranked_messages rm
    LEFT JOIN unread_counts uc ON
        (CASE
            WHEN rm.message_type = 'PRIVATE' THEN IF(rm.sender_id = :userId, rm.recipient_id, rm.sender_id)
            ELSE rm.group_id
        END) = uc.conversation_id
    WHERE rm.rn = 1
    ORDER BY rm.timestamp DESC
    """, nativeQuery = true)
    List<Map<String, Object>> findConversationsByUserId(@Param("userId") Long userId, @Param("groupIds") List<Long> groupIds);
}
