package com.example.messageservice.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ 配置类
 * 用于定义交换机、队列和它们之间的绑定关系
 */
@Configuration
public class RabbitMQConfig {

    // 定义交换机的名称
    public static final String EXCHANGE_NAME = "im-exchange";

    // 定义队列的名称
    public static final String QUEUE_NAME = "im-queue";

    // 定义路由键 (Routing Key) 的模式
    // im.message.# 表示可以匹配所有以 im.message. 开头的路由键
    public static final String ROUTING_KEY_PATTERN = "im.message.#";

    /**
     * 创建一个主题类型的交换机 (Topic Exchange)
     * @return TopicExchange
     */
    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }
    @Bean
    public org.springframework.amqp.support.converter.MessageConverter jsonMessageConverter() {
        return new org.springframework.amqp.support.converter.Jackson2JsonMessageConverter();
    }


    /**
     * 创建一个持久化的队列 (Durable Queue)
     * @return Queue
     */
    @Bean
    public Queue queue() {
        return new Queue(QUEUE_NAME);
    }

    /**
     * 创建一个绑定 (Binding)
     * 将我们定义的队列和交换机绑定在一起，并指定路由键模式
     * @param queue 队列Bean
     * @param exchange 交换机Bean
     * @return Binding
     */
    @Bean
    public Binding binding(Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(ROUTING_KEY_PATTERN);
    }
}
