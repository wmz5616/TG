package com.example.messageservice.controller;

import com.example.messageservice.service.PresenceService;
import org.springframework.beans.factory.annotation.Autowired;
<<<<<<< HEAD
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
=======
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
>>>>>>> 1a87df0d7045169a8a3e9611973c7c556173448b

@RestController
@RequestMapping("/api/presence")
public class PresenceController {

    @Autowired
    private PresenceService presenceService;

    @GetMapping("/status/{userId}")
    public String getStatus(@PathVariable Long userId) {
        return presenceService.getUserStatus(userId);
    }
<<<<<<< HEAD

    // 2. 添加这个新的批量查询接口
    @GetMapping("/status/batch")
    public Map<Long, String> getStatuses(@RequestParam List<Long> userIds) {
        return presenceService.getUsersStatus(userIds);
    }
=======
>>>>>>> 1a87df0d7045169a8a3e9611973c7c556173448b
}
