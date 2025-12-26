Distributed Instant Messaging System (基于微服务的即时通讯系统)

📖 项目简介

这是一个基于 Spring Boot 微服务架构开发的分布式即时通讯系统。项目实现了类似 Telegram 的核心功能，包括用户注册登录、单聊、群聊、实时消息推送、文件上传等功能。

系统采用 Spring Cloud 进行服务治理，RabbitMQ 处理消息异步解耦，WebSocket (STOMP) 实现实时双向通信，MinIO 作为对象存储服务。

🏗 系统架构

项目包含以下核心微服务模块：

服务名称

目录名

端口 (默认)

描述

API Gateway

api-gateway

8080

网关服务。负责请求路由、负载均衡、统一鉴权 (JWT) 和跨域配置。

User Service

user-service

8081

用户服务。处理用户注册、登录、JWT 签发、用户信息管理。

Message Service

message-service

8082

消息服务。核心模块，处理 WebSocket 连接、消息路由、存储及 RabbitMQ 消息投递。

Group Service

group-service

8083

群组服务。管理群组创建、成员邀请、群信息的维护。

File Service

file-service

8084

文件服务。集成 MinIO，处理聊天中的图片、文件上传与下载。

技术栈

后端框架: Spring Boot, Spring Cloud

构建工具: Gradle

数据库: MySQL (推荐), JPA/Hibernate

消息队列: RabbitMQ (用于服务间解耦及削峰填谷)

实时通信: WebSocket + STOMP协议

文件存储: MinIO

服务调用: OpenFeign

安全验证: Spring Security + JWT

✨ 核心功能

用户体系

用户注册与登录 (基于 JWT)。

个人信息修改。

即时通讯

基于 WebSocket 的实时消息推送。

支持文本、Emoji 表情发送。

消息状态追踪 (已读/未读，见 ReadReceiptPayload)。

用户在线状态管理 (PresenceService)。

群组功能

创建群组。

邀请成员加入群组。

群组成员管理。

文件传输

集成 MinIO 对象存储。

图片及文件上传接口。

🚀 快速开始

1. 环境准备

确保你的本地环境已安装以下依赖：

JDK 17+

Gradle 8.0+ (或使用项目自带的 ./gradlew)

Docker (用于快速启动中间件)

2. 启动中间件

你需要启动 MySQL, RabbitMQ 和 MinIO。推荐使用 Docker Compose (示例):

version: '3'
services:
  mysql:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: root
      MYSQL_DATABASE: im_db
    ports:
      - "3306:3306"
  
  rabbitmq:
    image: rabbitmq:management
    ports:
      - "5672:5672"
      - "15672:15672"

  minio:
    image: minio/minio
    command: server /data
    ports:
      - "9000:9000"
      - "9001:9001"


注意: 启动后，请根据各服务 src/main/resources/application.properties (或 .yml) 中的配置，在本地创建相应的数据库和配置 RabbitMQ/MinIO 的凭证。

3. 修改配置

检查各模块下的 application.properties 或 application.yml 文件，确保数据库连接、RabbitMQ 地址和 MinIO 密钥与你的本地环境一致。

API Gateway: 检查路由配置。

File Service: 修改 minio.url, access-key, secret-key。

User/Group/Message Service: 修改 spring.datasource.url 和 spring.rabbitmq.host。

4. 编译与运行

项目使用 Gradle 进行构建。你可以分模块启动，也可以在根目录统一构建。

使用 Gradle Wrapper 构建:

./gradlew clean build -x test


按顺序启动服务:

启动 User Service

启动 Group Service

启动 File Service

启动 Message Service

最后启动 API Gateway

5. 接口测试

项目包含 .http 测试文件 (IntelliJ IDEA HTTP Client)，位于各服务的根目录下 (例如 user-service/test.http)。你可以直接使用这些文件测试 API 接口。

此外，message-service/src/main/resources/static/chat.html 提供了一个简易的 WebSocket 聊天测试页面。

📂 项目结构概览

├── api-gateway            # 网关服务 (Auth Filter, Routing)
├── user-service           # 用户服务 (Auth, User Profile)
├── group-service          # 群组服务 (Group logic)
├── message-service        # 消息服务 (WebSocket, RabbitMQ, Chat Logic)
└── file-service           # 文件服务 (MinIO integration)


🤝 贡献

欢迎提交 Issue 和 Pull Request！

📄 License

MIT
