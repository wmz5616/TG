package com.example.messageservice.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ 配置类 (已重构)
 * 为不同类型的消息定义了独立的队列和路由规则
 */
@Configuration
public class RabbitMQConfig {

    // 交换机名称 (保持不变)
    public static final String EXCHANGE_NAME = "im-exchange";

    // ----- 【新增】专用于处理用户资料更新、上下线等事件的队列 -----
    public static final String USER_EVENTS_QUEUE_NAME = "user-events-queue";
    public static final String USER_EVENTS_ROUTING_KEY = "user.#"; // 匹配所有以 "user." 开头的路由键

    // ----- 【修改】专用于处理聊天消息的队列 -----
    // 为了清晰，我们将原来的 QUEUE_NAME 重命名
    public static final String MESSAGE_QUEUE_NAME = "message-queue";
    public static final String MESSAGE_ROUTING_KEY = "im.message.#"; // 匹配所有以 "im.message." 开头的路由键

    /**
     * 创建一个主题类型的交换机 (Topic Exchange)
     */
    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    /**
     * 【【新增】】创建用户事件队列
     */
    @Bean
    public Queue userEventsQueue() {
        return new Queue(USER_EVENTS_QUEUE_NAME);
    }

    /**
     * 【【新增】】将用户事件队列绑定到交换机
     * 所有路由键以 "user." 开头的消息都会被路由到这个队列
     */
    @Bean
    public Binding userEventsBinding(Queue userEventsQueue, TopicExchange exchange) {
        return BindingBuilder.bind(userEventsQueue).to(exchange).with(USER_EVENTS_ROUTING_KEY);
    }

    /**
     * 【【修改】】创建聊天消息队列 (原im-queue)
     */
    @Bean
    public Queue messageQueue() {
        return new Queue(MESSAGE_QUEUE_NAME);
    }

    /**
     * 【【修改】】将聊天消息队列绑定到交换机
     * 所有路由键以 "im.message." 开头的消息都会被路由到这个队列
     */
    @Bean
    public Binding messageBinding(Queue messageQueue, TopicExchange exchange) {
        return BindingBuilder.bind(messageQueue).to(exchange).with(MESSAGE_ROUTING_KEY);
    }

    /**
     * 定义一个全局的消息转换器，让 RabbitMQ 可以处理 JSON 格式的对象
     */
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
