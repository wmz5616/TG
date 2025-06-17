package com.example.messageservice.service;
import java.util.List;
import java.util.Map; // <-- 1. 导入 Map

public interface PresenceService {
    String getUserStatus(Long userId);
    Map<Long, String> getUsersStatus(List<Long> userIds);
}
