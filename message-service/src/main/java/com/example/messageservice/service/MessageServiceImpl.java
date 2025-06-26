package com.example.messageservice.service;

import com.example.messageservice.client.GroupClient;
import com.example.messageservice.client.UserClient;
import com.example.messageservice.config.RabbitMQConfig;
import com.example.messageservice.dto.ConversationDTO;
import com.example.messageservice.dto.GroupDTO;
import com.example.messageservice.dto.MessageStatusUpdate;
import com.example.messageservice.dto.ReadReceiptPayload;
import com.example.messageservice.model.Message;
import com.example.messageservice.model.MessageType;
import com.example.messageservice.model.MessageStatus;
import com.example.messageservice.model.UserDTO;
import com.example.messageservice.repository.MessageRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.*;
import java.sql.Timestamp;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Service
public class MessageServiceImpl implements MessageService {
    private final MessageRepository messageRepository;
    private final UserClient userClient;
    private final GroupClient groupClient;
    private final RabbitTemplate rabbitTemplate;
    private final SimpMessagingTemplate messagingTemplate; // 【新增】用于发送状态更新

    // 构造函数注入所有依赖
    public MessageServiceImpl(MessageRepository messageRepository,
                              UserClient userClient,
                              GroupClient groupClient,
                              RabbitTemplate rabbitTemplate,
                              SimpMessagingTemplate messagingTemplate) {
        this.messageRepository = messageRepository;
        this.userClient = userClient;
        this.groupClient = groupClient;
        this.rabbitTemplate = rabbitTemplate;
        this.messagingTemplate = messagingTemplate;
    }
    @Override
    public void processAndSendMessage(Message message) {
        message.setStatus(MessageStatus.SENT);
        String routingKey = MessageType.PRIVATE.equals(message.getMessageType()) ? "im.message.private" : "im.message.group";
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, routingKey, message);
    }
    @Override
    public void markMessagesAsRead(ReadReceiptPayload payload) {
        List<Message> messages = messageRepository.findAllById(payload.getMessageIds());
        for (Message message : messages) {
            boolean isRecipient = message.getRecipientId() != null && message.getRecipientId().equals(payload.getReaderId());
            boolean isGroupMember = message.getGroupId() != null; // 简化处理
            if ((isRecipient || isGroupMember) && message.getStatus() != MessageStatus.READ) {
                message.setStatus(MessageStatus.READ);
                messageRepository.save(message);
                MessageStatusUpdate statusUpdate = new MessageStatusUpdate(message.getId(), MessageStatus.READ);
                messagingTemplate.convertAndSendToUser(
                        message.getSenderId().toString(),
                        "/queue/status",
                        statusUpdate
                );
            }
        }
    }
    @Override
    public Message sendMessage(Message message) {
        processAndSendMessage(message);
        return message;
    }
    @Override
    public List<Message> getChatHistory(Long user1Id, Long user2Id) {
        return messageRepository.findChatHistory(user1Id, user2Id);
    }
    @Override
    public List<Message> getGroupChatHistory(Long groupId) {
        return messageRepository.findByGroupIdOrderByTimestampAsc(groupId);
    }
    @Override
    public List<Long> getPrivateConversationPartnerIds(Long userId) {
        return messageRepository.findPrivateConversationPartnerIds(userId);
    }
    @Override
    public Message saveAndBroadcastMessage(Message message) {
        Message savedMessage = messageRepository.save(message);
        if (MessageType.PRIVATE.equals(savedMessage.getMessageType())) {
            messagingTemplate.convertAndSendToUser(savedMessage.getRecipientId().toString(), "/queue/messages", savedMessage);
            messagingTemplate.convertAndSendToUser(savedMessage.getSenderId().toString(), "/queue/messages", savedMessage);
        } else if (MessageType.GROUP.equals(savedMessage.getMessageType())) {
            messagingTemplate.convertAndSend("/topic/group/" + savedMessage.getGroupId(), savedMessage);
        }
        return savedMessage;
    }
    @Override
    public List<ConversationDTO> getConversations(Long userId) {
        List<Long> groupIds = groupClient.getGroupIdsForUser(userId);
        List<Long> privatePartnerIds = messageRepository.findPrivateConversationPartnerIds(userId);
        List<Long> allUserIdsToFetch = new ArrayList<>(privatePartnerIds);
        List<Long> allGroupIdsToFetch = new ArrayList<>(groupIds);
        Map<Long, UserDTO> userInfoMap = allUserIdsToFetch.isEmpty() ? Collections.emptyMap() :
                userClient.getUsersByIds(allUserIdsToFetch).stream()
                        .collect(Collectors.toMap(UserDTO::getId, Function.identity()));

        Map<Long, GroupDTO> groupInfoMap = allGroupIdsToFetch.isEmpty() ? Collections.emptyMap() :
                groupClient.getGroupsByIds(allGroupIdsToFetch).stream()
                        .collect(Collectors.toMap(GroupDTO::getId, Function.identity()));
        List<Map<String, Object>> messageMetas = messageRepository.findConversationsByUserId(userId, groupIds.isEmpty() ? List.of(-1L) : groupIds);
        Map<Long, Map<String, Object>> messageMetaMap = messageMetas.stream()
                .collect(Collectors.toMap(
                        row -> ((Number) row.get("conversationId")).longValue(),
                        Function.identity()
                ));
        Stream<ConversationDTO> privateConvos = privatePartnerIds.stream().map(partnerId -> {
            UserDTO partnerInfo = userInfoMap.get(partnerId);
            if (partnerInfo == null) return null;
            Map<String, Object> meta = messageMetaMap.get(partnerId);
            String lastMessage = meta != null ? formatLastMessageForList((String)meta.get("lastMessageContent")) : "开始聊天吧";
            LocalDateTime timestamp = meta != null && meta.get("lastMessageTimestamp") != null ? ((Timestamp) meta.get("lastMessageTimestamp")).toLocalDateTime() : null;
            Long unreadCount = meta != null ? ((Number) meta.get("unreadCount")).longValue() : 0L;
            return new ConversationDTO(partnerId, MessageType.PRIVATE, partnerInfo.getUsername(), partnerInfo.getAvatarUrl(), lastMessage, timestamp, unreadCount);
        }).filter(Objects::nonNull);
        Stream<ConversationDTO> groupConvos = groupIds.stream().map(groupId -> {
            GroupDTO groupInfo = groupInfoMap.get(groupId);
            if (groupInfo == null) return null;
            Map<String, Object> meta = messageMetaMap.get(groupId);
            String lastMessage = meta != null ? formatLastMessageForList((String)meta.get("lastMessageContent")) : "群聊已创建";
            LocalDateTime timestamp = meta != null && meta.get("lastMessageTimestamp") != null ? ((Timestamp) meta.get("lastMessageTimestamp")).toLocalDateTime() : null;
            Long unreadCount = meta != null ? ((Number) meta.get("unreadCount")).longValue() : 0L;
            if (timestamp == null) {
            }
            return new ConversationDTO(groupId, MessageType.GROUP, groupInfo.getName(), null, lastMessage, timestamp, unreadCount);
        }).filter(Objects::nonNull);
        return Stream.concat(privateConvos, groupConvos)
                .sorted(Comparator.comparing(
                        ConversationDTO::getLastMessageTimestamp,
                        Comparator.nullsLast(Comparator.reverseOrder())
                ))
                .collect(Collectors.toList());
    }
    private String formatLastMessageForList(String content) {
        if (content == null || content.isBlank()) return "";
        String trimmedContent = content.trim();
        if (trimmedContent.matches("^https?://.*\\.(jpeg|jpg|png|gif|bmp|webp)$") || trimmedContent.contains("<img") || trimmedContent.contains("message-image-container")) {
            return "[图片]";
        }
        if (trimmedContent.contains("<a href")) return "[文件]";
        if (trimmedContent.contains("/emojis/")) return "[表情]";
        String plainText = trimmedContent.replaceAll("<[^>]*>", "");
        return plainText.length() > 20 ? plainText.substring(0, 20) + "..." : plainText;
    }
}
