package com.example.messageservice.listener;

import com.example.messageservice.config.RabbitMQConfig;
import com.example.messageservice.model.Message;
import com.example.messageservice.model.MessageType;
import com.example.messageservice.repository.MessageRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class MessageListener {

    private final MessageRepository messageRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public MessageListener(MessageRepository messageRepository, SimpMessagingTemplate messagingTemplate) {
        this.messageRepository = messageRepository;
        this.messagingTemplate = messagingTemplate;
    }

    @RabbitListener(queues = RabbitMQConfig.MESSAGE_QUEUE_NAME)
    public void handleMessage(Message message) {
        // 1. 将消息存入数据库，这一步会为 message 对象生成 ID 和 timestamp
        Message savedMessage = messageRepository.save(message);

        // 2. 根据消息类型，通过WebSocket推送到目的地
        if (MessageType.PRIVATE.equals(savedMessage.getMessageType())) {

            messagingTemplate.convertAndSendToUser(
                    savedMessage.getRecipientId().toString(),
                    "/queue/messages",
                    savedMessage
            );

            // B. 也把这条已保存的消息推给发送者自己，用于多端同步和实时UI更新
            messagingTemplate.convertAndSendToUser(
                    savedMessage.getSenderId().toString(),
                    "/queue/messages",
                    savedMessage
            );

        } else if (MessageType.GROUP.equals(savedMessage.getMessageType())) {
            // 群聊消息直接广播到群组的 topic，所有订阅者（包括发送者）都会收到
            messagingTemplate.convertAndSend("/topic/group/" + savedMessage.getGroupId(), savedMessage);
        }
    }
}
