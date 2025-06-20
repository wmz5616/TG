package com.example.messageservice.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/stickers") // Controller的基础路径
public class StickerController {

    private final String TELEGRAM_API_URL = "https://api.telegram.org/bot7602124228:AAFz5xjjkZVCrhd2Fqgxsbho5J_NsVCYddI/";
    private final RestTemplate restTemplate = new RestTemplate();

    // ... getStickerSet 和 getFile 方法保持不变 ...
    @GetMapping("/set/{setName}")
    public ResponseEntity<String> getStickerSet(@PathVariable String setName) {
        try {
            String url = TELEGRAM_API_URL + "getStickerSet?name=" + setName;
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

    // 【【关键】】请确保这个方法的路径拼写完全正确
    @GetMapping("/file-proxy/{filePath:.+}")
    public ResponseEntity<byte[]> proxyTelegramFile(@PathVariable String filePath) {
        try {
            // Telegram下载文件的URL格式是 /file/bot...
            String fileUrl = TELEGRAM_API_URL.replace("/bot", "/file/bot") + filePath;
            byte[] fileBytes = restTemplate.getForObject(fileUrl, byte[].class);

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(fileBytes);

        } catch (Exception e) {
            System.err.println("代理请求Telegram文件失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
