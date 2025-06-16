package com.example.messageservice.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Autowired
    private HttpHandshakeInterceptor handshakeInterceptor;

<<<<<<< HEAD
=======
    // 注入我们新的通道拦截器
>>>>>>> 1a87df0d7045169a8a3e9611973c7c556173448b
    @Autowired
    private PresenceChannelInterceptor presenceChannelInterceptor;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
<<<<<<< HEAD
        // 【关键修改】: 在这里为WebSocket端点单独配置CORS
        registry.addEndpoint("/ws")
                .addInterceptors(handshakeInterceptor)
                .setAllowedOriginPatterns("*") // 允许所有来源的WebSocket连接
                .withSockJS();
=======
        registry.addEndpoint("/ws").addInterceptors(handshakeInterceptor).withSockJS();
>>>>>>> 1a87df0d7045169a8a3e9611973c7c556173448b
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes("/app");
<<<<<<< HEAD
        registry.enableSimpleBroker("/topic", "/queue","/user");
    }

=======
        registry.enableSimpleBroker("/topic", "/queue");
    }

    // 重写这个方法，来注册我们的通道拦截器
>>>>>>> 1a87df0d7045169a8a3e9611973c7c556173448b
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(presenceChannelInterceptor);
    }
}
