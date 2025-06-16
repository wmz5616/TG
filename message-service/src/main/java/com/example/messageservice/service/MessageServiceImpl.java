package com.example.messageservice.service;

import com.example.messageservice.client.GroupClient;
import com.example.messageservice.client.UserClient;
<<<<<<< HEAD
import com.example.messageservice.config.RabbitMQConfig;
import com.example.messageservice.dto.MessageStatusUpdate;
import com.example.messageservice.dto.ReadReceiptPayload;
import com.example.messageservice.model.Message;
import com.example.messageservice.model.MessageType;
import com.example.messageservice.model.MessageStatus;
import com.example.messageservice.repository.MessageRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
=======
import com.example.messageservice.model.Message;
import com.example.messageservice.model.MessageType;
import com.example.messageservice.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List; // Make sure this import is present
>>>>>>> 1a87df0d7045169a8a3e9611973c7c556173448b

@Service
public class MessageServiceImpl implements MessageService {

<<<<<<< HEAD
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

=======
    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserClient userClient;

    @Autowired
    private GroupClient groupClient;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Override
    public Message sendMessage(Message message) {
        if (MessageType.PRIVATE.equals(message.getMessageType())) {
            return handlePrivateMessage(message);
        } else if (MessageType.GROUP.equals(message.getMessageType())) {
            return handleGroupMessage(message);
        } else {
            throw new IllegalArgumentException("不支持的消息类型: " + message.getMessageType());
        }
    }

    // This is the missing method. Add it back.
>>>>>>> 1a87df0d7045169a8a3e9611973c7c556173448b
    @Override
    public List<Message> getChatHistory(Long user1Id, Long user2Id) {
        return messageRepository.findChatHistory(user1Id, user2Id);
    }

<<<<<<< HEAD
    @Override
    public List<Message> getGroupChatHistory(Long groupId) {
        return messageRepository.findByGroupIdOrderByTimestampAsc(groupId);
    }

    @Override
    public List<Long> getPrivateConversationPartnerIds(Long userId) {
        return messageRepository.findPrivateConversationPartnerIds(userId);
=======
    private Message handlePrivateMessage(Message message) {
        Object sender = userClient.getUserById(message.getSenderId());
        if (sender == null) {
            throw new IllegalArgumentException("发送者用户不存在，ID: " + message.getSenderId());
        }
        Object recipient = userClient.getUserById(message.getRecipientId());
        if (recipient == null) {
            throw new IllegalArgumentException("接收者用户不存在，ID: " + message.getRecipientId());
        }

        Message savedMessage = messageRepository.save(message);
        String destination = "/queue/messages/" + savedMessage.getRecipientId();
        messagingTemplate.convertAndSend(destination, savedMessage);
        System.out.println("私聊消息已发送: " + savedMessage);
        return savedMessage;
    }

    private Message handleGroupMessage(Message message) {
        Long senderId = message.getSenderId();
        Long groupId = message.getGroupId();

        boolean isMember = groupClient.isUserMember(groupId, senderId);
        if (!isMember) {
            throw new IllegalArgumentException("发送失败，用户 " + senderId + " 不是群 " + groupId + " 的成员。");
        }

        Message savedMessage = messageRepository.save(message);

        String destination = "/topic/group/" + groupId;
        messagingTemplate.convertAndSend(destination, savedMessage);
        System.out.println("群聊消息已发送: " + savedMessage);
        return savedMessage;
>>>>>>> 1a87df0d7045169a8a3e9611973c7c556173448b
    }
}
