package com.example.apigateway.filter;

import com.example.apigateway.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    @Autowired
    private JwtUtil jwtUtil;

    private final List<String> publicRoutes = List.of(
            "/api/auth/register",
            "/api/auth/login",
            "/chat.html",
            "/api/stickers/file-proxy" // <-- 新增这一行，允许对贴纸代理的匿名访问
    );

    public AuthenticationFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            String path = exchange.getRequest().getURI().getPath();

            if (publicRoutes.stream().anyMatch(path::startsWith)) {
                return chain.filter(exchange);
            }

            String token = null;
            HttpHeaders headers = exchange.getRequest().getHeaders();
            if (headers.containsKey(HttpHeaders.AUTHORIZATION)) {
                String authHeader = headers.getFirst(HttpHeaders.AUTHORIZATION);
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    token = authHeader.substring(7);
                }
            }

            if (token == null && path.startsWith("/ws")) {
                token = exchange.getRequest().getQueryParams().getFirst("token");
            }

            if (token == null) {
                return unauthorizedResponse(exchange);
            }

            try {
                if (!jwtUtil.validateToken(token)) {
                    return unauthorizedResponse(exchange);
                }
            } catch (Exception e) {
                return unauthorizedResponse(exchange);
            }

            // 【【关键修正】】
            // 1. 提取用户信息
            String username = jwtUtil.extractUsername(token);
            Long userId = jwtUtil.extractUserId(token);

            // 2. 创建一个新的请求，并在其头部添加用户信息
            ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
                    .header("X-Authenticated-Username", username)
                    .header("X-Authenticated-User-Id", String.valueOf(userId))
                    .build();

            // 3. 用这个被修改过的新请求，创建一个新的 ServerWebExchange
            ServerWebExchange modifiedExchange = exchange.mutate().request(modifiedRequest).build();

            // 4. 将这个包含了新请求头的 exchange 传递给下一个过滤器
            return chain.filter(modifiedExchange);
        };
    }

    private Mono<Void> unauthorizedResponse(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }

    public static class Config {}
}
