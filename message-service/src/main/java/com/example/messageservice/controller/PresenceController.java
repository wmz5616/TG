package com.example.messageservice.controller;

import com.example.messageservice.service.PresenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/presence")
public class PresenceController {

    @Autowired
    private PresenceService presenceService;

    @GetMapping("/status/{userId}")
    public String getStatus(@PathVariable Long userId) {
        return presenceService.getUserStatus(userId);
    }

    // 2. 添加这个新的批量查询接口
    @GetMapping("/status/batch")
    public Map<Long, String> getStatuses(@RequestParam List<Long> userIds) {
        return presenceService.getUsersStatus(userIds);
    }
}
