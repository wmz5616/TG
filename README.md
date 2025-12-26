# **⚡️ 分布式即时通讯系统 (Distributed IM System)**

一个基于 Spring Cloud 微服务架构的高性能即时通讯平台，支持单聊、群聊、文件传输及实时消息推送。

## **🏗️ 系统全景图**

本项目采用微服务拆分，各服务职责明确，通过 API 网关统一入口。

| 服务名称 | 端口 | 核心职责 | 关键技术 |
| :---- | :---- | :---- | :---- |
| **API Gateway** | 8080 | 流量入口、路由转发、JWT 统一鉴权、跨域处理 | Spring Cloud Gateway |
| **User Service** | 8081 | 用户注册/登录、JWT 签发、个人信息管理 | Spring Security, JPA |
| **Message Service** | 8082 | **核心**：WebSocket 连接、消息路由、离线消息存储 | WebSocket (STOMP), RabbitMQ |
| **Group Service** | 8083 | 群组创建、成员邀请、群信息维护 | OpenFeign |
| **File Service** | 8084 | 文件/图片上传与下载 | MinIO SDK |

## **✨ 核心功能亮点**

* 🔐 **安全认证**: 基于 JWT 的无状态认证，网关层统一拦截非公开接口。  
* 💬 **实时通讯**: 使用 WebSocket (STOMP 协议) 实现低延迟双向通信。  
* 📬 **消息可靠性**: 引入 **RabbitMQ** 处理消息削峰填谷，确保高并发下消息不丢失。  
* 📂 **对象存储**: 集成 **MinIO**，自建高性能分布式文件服务，支持图片/文件发送。  
* 👥 **群组交互**: 完整的建群、拉人、群聊广播机制。  
* 👀 **状态感知**: 支持用户在线/离线状态检测 (PresenceService) 及消息已读回执。

## **🚀 3分钟快速启动**

### **1\. 环境准备 (Prerequisites)**

请确保本地已安装以下环境：

* **JDK 17+**  
* **Gradle 8.0+**  
* **Docker & Docker Compose** (推荐)

### **2\. 启动中间件 (Infrastructure)**

项目依赖 MySQL, RabbitMQ 和 MinIO。使用 Docker 快速拉起：

\# 启动 MySQL, RabbitMQ, MinIO  
docker run \-d \--name mysql \-p 3306:3306 \-e MYSQL\_ROOT\_PASSWORD=root mysql:8.0  
docker run \-d \--name rabbitmq \-p 5672:5672 \-p 15672:15672 rabbitmq:management  
docker run \-d \--name minio \-p 9000:9000 \-p 9001:9001 minio/minio server /data \--console-address ":9001"

**⚠️ 注意**: 启动后，请务必手动创建数据库 im\_db，并在 MinIO 控制台 (localhost:9001) 创建 Bucket（如 im-files）及 Access Keys。

### **3\. 配置文件修改 (Configuration)**

你需要修改各服务 src/main/resources/application.properties 中的关键配置以匹配你的本地环境：

* **Database**: spring.datasource.url, username, password  
* **RabbitMQ**: spring.rabbitmq.host, port  
* **MinIO** (仅 File Service): minio.endpoint, access-key, secret-key

### **4\. 编译与运行 (Build & Run)**

在项目根目录下执行：

\# 编译所有模块（跳过测试）  
./gradlew clean build \-x test

**建议启动顺序：**

1. User Service (8081)  
2. Group Service (8083)  
3. File Service (8084)  
4. Message Service (8082)  
5. **API Gateway (8080)** \-\> *最后启动，作为统一访问入口*

## **🛠️ 接口测试指南**

项目内置了 **IntelliJ HTTP Client** 测试脚本，无需 Postman 即可直接测试。

1. **用户注册/登录**: 打开 user-service/test.http，运行 POST /auth/register 和 POST /auth/login。  
2. **获取 Token**: 登录接口会返回 JWT Token，请复制该 Token。  
3. **WebSocket 测试**:  
   * 启动所有服务。  
   * 浏览器打开 message-service/src/main/resources/static/chat.html。  
   * 填入 Token 连接 WebSocket，即可开始模拟发送消息。

## **📂 项目目录结构**

Root  
├── api-gateway          \# 🌍 网关层  
├── user-service         \# 👤 用户领域模型  
├── group-service        \# 👥 群组领域模型  
├── message-service      \# 📨 消息核心 (Chat, WebSocket)  
├── file-service         \# 📁 文件存储适配  
└── docker-compose.yml   \# 🐳 (可选) 容器编排文件

## **🤝 贡献与支持**

如果你发现 Bug 或有新功能建议，欢迎提交 Issue 或 Pull Request。

**License**: MIT