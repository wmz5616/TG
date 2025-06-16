// 文件路径: api-gateway/src/main/java/com/example/apigateway/ApiGatewayApplication.java

package com.example.apigateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class ApiGatewayApplication {

    public static void main(String[] args) {
        // 改回这一行，删我们测试用的 throw new RuntimeException
        SpringApplication.run(ApiGatewayApplication.class, args);
    }
}
