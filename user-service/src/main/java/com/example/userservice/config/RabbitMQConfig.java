package com.example.userservice.config;

import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 为 User-Service 的 RabbitTemplate 配置JSON消息转换器
 */
@Configuration
public class RabbitMQConfig {

    /**
     * 定义一个Bean，告诉Spring使用Jackson库来转换消息。
     * 这样，当RabbitTemplate发送一个对象时，它会被序列化成JSON字符串。
     * @return 一个配置好的消息转换器实例
     */
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
