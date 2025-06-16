package com.example.messageservice.config;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
<<<<<<< HEAD
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
=======
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

>>>>>>> 1a87df0d7045169a8a3e9611973c7c556173448b
import java.util.Map;

@Component
public class HttpHandshakeInterceptor implements HandshakeInterceptor {
<<<<<<< HEAD
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        // 这个拦截器现在只负责日志打印，不再处理业务逻辑
        System.out.println("WebSocket Handshake initiated from: " + request.getRemoteAddress());
=======

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        // 1. 添加一个非常醒目的日志，来确认这个方法被触发了
        System.out.println("*************************************************");
        System.out.println("********** HANDSHAKE INTERCEPTOR RUNNING **********");
        System.out.println("*************************************************");

        if (request instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
            // 2. 从 URL 查询参数中获取 "userId"
            String userId = servletRequest.getServletRequest().getParameter("userId");
            // 3. 打印出我们从URL中获取到的值，无论它是不是null
            System.out.println("********** Interceptor found userId in URL: [" + userId + "] **********");

            if (userId != null) {
                // 4. 将 userId 放入 WebSocket session 的 attributes 中
                attributes.put("userId", userId);
                System.out.println("********** userId [" + userId + "] ADDED TO ATTRIBUTES MAP **********");
            }
        }
>>>>>>> 1a87df0d7045169a8a3e9611973c7c556173448b
        return true;
    }

    @Override
<<<<<<< HEAD
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {}
=======
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
        // Do nothing
    }
>>>>>>> 1a87df0d7045169a8a3e9611973c7c556173448b
}
