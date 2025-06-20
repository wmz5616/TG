package com.example.messageservice.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate; // 确保导入了这个包

@RestController
@RequestMapping("/api/stickers")
public class StickerController {

    // 【【请再次确认】】这里的Token是您自己的真实Token
    private final String TELEGRAM_API_URL = "https://api.telegram.org/bot7602124228:AAFz5xjjkZVCrhd2Fqgxsbho5J_NsVCYddI/";

    // 【【关键修正】】在这里声明并创建一个 RestTemplate 实例
    private final RestTemplate restTemplate = new RestTemplate();

    @GetMapping("/set/{setName}")
    public ResponseEntity<String> getStickerSet(@PathVariable String setName) {
        try {
            String url = TELEGRAM_API_URL + "getStickerSet?name=" + setName;
            // 现在程序认识 restTemplate 是什么了
            String response = restTemplate.getForObject(url, String.class);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("请求Telegram贴纸包API失败: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"ok\":false, \"description\":\"Failed to fetch sticker set.\"}");
        }
    }

    @GetMapping("/file/{fileId}")
    public ResponseEntity<String> getFile(@PathVariable String fileId) {
        try {
            String url = TELEGRAM_API_URL + "getFile?file_id=" + fileId;
            String response = restTemplate.getForObject(url, String.class);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("请求Telegram文件路径API失败: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"ok\":false, \"description\":\"Failed to fetch file info.\"}");
        }
    }
}
