package com.example.messageservice.controller;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/emojis")
public class EmojiController {

    @GetMapping("/list")
    public ResponseEntity<List<String>> listEmojis() {
        try {
            // 这个解析器可以查找我们项目资源路径下的文件
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            // 查找 static/emojis/ 目录下的所有文件
            Resource[] resources = resolver.getResources("classpath:static/emojis/*");

            // 从找到的资源中，提取出文件名，并收集成一个列表
            List<String> fileNames = Arrays.stream(resources)
                    .map(Resource::getFilename)
                    .collect(Collectors.toList());

            // 将文件名列表作为JSON返回给前端
            return ResponseEntity.ok(fileNames);
        } catch (IOException e) {
            e.printStackTrace();
            // 如果发生错误，返回一个空的列表和服务器错误状态码
            return ResponseEntity.status(500).body(List.of());
        }
    }
}
