// 文件路径: api-gateway/src/main/java/com/example/apigateway/CorsHeaderCleanupFilter.java

package com.example.apigateway;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class CorsHeaderCleanupFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            HttpHeaders headers = exchange.getResponse().getHeaders();

            // 【【升级】】强制重写所有相关的CORS头，确保它们都是单一值
            headers.set(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "http://localhost:8081");
            headers.set(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
            headers.set(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, "*"); // 允许所有方法
            headers.set(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, "*"); // 允许所有头
            headers.set(HttpHeaders.ACCESS_CONTROL_MAX_AGE, "3600"); // 预检请求的有效期
        }));
    }

    @Override
    public int getOrder() {
        // 仍然使用最低优先级，确保在最后执行
        return Ordered.LOWEST_PRECEDENCE;
    }
}
