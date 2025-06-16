package com.example.messageservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients // <-- 新增：开启 Feign 功能
@EnableDiscoveryClient // <-- 新增：开启服务发现功能
@SpringBootApplication
public class MessageServiceApplication {

	public static void main(String[] args) {
<<<<<<< HEAD

=======
>>>>>>> 1a87df0d7045169a8a3e9611973c7c556173448b
		SpringApplication.run(MessageServiceApplication.class, args);
	}

}
