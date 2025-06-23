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
import java.math.BigInteger; // 【新增】导入
import java.sql.Timestamp; // 【新增】导入
import java.util.stream.Collectors; // 【新增】导入
import java.util.function.Function; // 【新增】导入


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

    // 【修改】这个方法现在是新消息的总入口
    @Override
    public void processAndSendMessage(Message message) {
        // 【注意】这里假设你的 Feign Client 中有 userExists 方法，如果没有需要自行添加
        // if (!userClient.userExists(message.getSenderId())) {
        //     throw new IllegalArgumentException("发送者用户不存在: " + message.getSenderId());
        // }

        if (MessageType.PRIVATE.equals(message.getMessageType())) {
            // 私聊消息的权限校验等...
        } else if (MessageType.GROUP.equals(message.getMessageType())) {
            // 群聊消息的权限校验等...
        }

        // 设置消息初始状态为 SENT
        message.setStatus(MessageStatus.SENT);

        // 将消息发送到RabbitMQ，让监听器去异步处理
        String routingKey = MessageType.PRIVATE.equals(message.getMessageType()) ? "im.message.private" : "im.message.group";
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, routingKey, message);
    }

    // 【新增】处理已读回执的核心逻辑
    @Override
    public void markMessagesAsRead(ReadReceiptPayload payload) {
        // 1. 根据ID列表从数据库查出所有相关的消息
        List<Message> messages = messageRepository.findAllById(payload.getMessageIds());

        for (Message message : messages) {
            // 2. 安全检查：确保是接收者本人将消息标记为已读
            boolean isRecipient = message.getRecipientId() != null && message.getRecipientId().equals(payload.getReaderId());
            // 对于群聊，任何群成员都可以触发（暂不实现，仅为示例）
            boolean isGroupMember = message.getGroupId() != null;

            if (isRecipient || isGroupMember) {
                // 3. 如果消息状态不是“已读”，则更新它
                if (message.getStatus() != MessageStatus.READ) {
                    message.setStatus(MessageStatus.READ);
                    messageRepository.save(message); // 4. 保存更新到数据库

                    // 5. 【关键】将“已读”这个状态更新，通知给原始发件人
                    MessageStatusUpdate statusUpdate = new MessageStatusUpdate(message.getId(), MessageStatus.READ);
                    messagingTemplate.convertAndSendToUser(
                            message.getSenderId().toString(), // 目标用户：发件人
                            "/queue/status",                  // 发送到他私有的状态队列
                            statusUpdate                      // 发送内容
                    );
                }
            }
        }
    }

    // --- 保留原有的查询历史记录等方法 ---
    @Override
    public Message sendMessage(Message message) {
        processAndSendMessage(message);
        // 立即返回原始消息给Controller，用于前端的即时UI更新
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
        // 1. 将消息存入数据库，这一步会为 message 对象生成 ID 和 timestamp
        Message savedMessage = messageRepository.save(message);

        // 2. 根据消息类型，通过WebSocket推送到目的地
        if (MessageType.PRIVATE.equals(savedMessage.getMessageType())) {
            // 推送给接收者
            messagingTemplate.convertAndSendToUser(
                    savedMessage.getRecipientId().toString(),
                    "/queue/messages",
                    savedMessage
            );
            // 推送回给发送者（用于多端同步和UI更新）
            messagingTemplate.convertAndSendToUser(
                    savedMessage.getSenderId().toString(),
                    "/queue/messages",
                    savedMessage
            );
        } else if (MessageType.GROUP.equals(savedMessage.getMessageType())) {
            // 群聊消息直接广播到群组的 topic
            messagingTemplate.convertAndSend("/topic/group/" + savedMessage.getGroupId(), savedMessage);
        }

        return savedMessage;
    }

    @Override
    public List<ConversationDTO> getConversations(Long userId) {
        // 1. 获取用户所在的群组ID列表
        List<Long> groupIds = new ArrayList<>();
        try {
            groupIds = groupClient.getGroupIdsForUser(userId);
        } catch (Exception e) {
            System.out.println("获取群组列表失败，可能用户未加入任何群组: " + e.getMessage());
        }
        if (groupIds.isEmpty()) {
            groupIds.add(0L); // 防止SQL查询因IN ()为空而报错
        }

        // 2. 调用数据库查询，获取基础会话信息
        List<Map<String, Object>> rawResults = messageRepository.findConversationsByUserId(userId, groupIds);
        if (rawResults.isEmpty()) {
            return Collections.emptyList();
        }

        // 3. 从结果中分离出需要查询详情的 ID 列表
        List<Long> userIdsToFetch = rawResults.stream()
                .filter(row -> "PRIVATE".equals(row.get("type")))
                .map(row -> ((Number) row.get("conversationId")).longValue())
                .collect(Collectors.toList());

        List<Long> groupIdsToFetch = rawResults.stream()
                .filter(row -> "GROUP".equals(row.get("type")))
                .map(row -> ((Number) row.get("conversationId")).longValue())
                .collect(Collectors.toList());

        // 4. 【【核心修改】】批量获取用户和群组的详细信息
        // 我们先声明变量，然后在下面的逻辑中只对它进行一次赋值
        Map<Long, UserDTO> userInfoMap;
        if (!userIdsToFetch.isEmpty()) {
            try {
                userInfoMap = userClient.getUsersByIds(userIdsToFetch).stream()
                        .collect(Collectors.toMap(UserDTO::getId, Function.identity()));
            } catch (Exception e) {
                System.out.println("批量获取用户信息失败: " + e.getMessage());
                userInfoMap = Collections.emptyMap(); // 出错时赋一个空值
            }
        } else {
            userInfoMap = Collections.emptyMap(); // 列表为空时赋一个空值
        }

        // 对 groupInfoMap 进行同样的操作
        Map<Long, GroupDTO> groupInfoMap;
        if (!groupIdsToFetch.isEmpty()){
            try {
                groupInfoMap = groupClient.getGroupsByIds(groupIdsToFetch).stream()
                        .collect(Collectors.toMap(GroupDTO::getId, Function.identity()));
            } catch (Exception e) {
                System.out.println("批量获取群组信息失败: " + e.getMessage());
                groupInfoMap = Collections.emptyMap();
            }
        } else {
            groupInfoMap = Collections.emptyMap();
        }


        // 5. 最终组装
        Map<Long, UserDTO> finalUserInfoMap = userInfoMap;
        Map<Long, GroupDTO> finalGroupInfoMap = groupInfoMap;
        return rawResults.stream().map(row -> {
            Long conversationId = ((Number) row.get("conversationId")).longValue();
            String typeStr = (String) row.get("type");
            MessageType type = MessageType.valueOf(typeStr);

            String name = "未知";
            String avatarUrl = "";

            if (type == MessageType.PRIVATE) {
                UserDTO user = finalUserInfoMap.get(conversationId);
                if (user != null) {
                    name = user.getUsername();
                    avatarUrl = user.getAvatarUrl();
                }
            } else { // GROUP
                GroupDTO group = finalGroupInfoMap.get(conversationId);
                if (group != null) {
                    name = group.getName();
                    // 暂无群头像，可预留
                }
            }

            String lastMessageContent = (String) row.get("lastMessageContent");
            LocalDateTime lastMessageTimestamp = row.get("lastMessageTimestamp") != null ? ((Timestamp) row.get("lastMessageTimestamp")).toLocalDateTime() : null;
            Long unreadCount = ((Number) row.get("unreadCount")).longValue();

            return new ConversationDTO(conversationId, type, name, avatarUrl, lastMessageContent, lastMessageTimestamp, unreadCount);
        }).collect(Collectors.toList());
    }
}
