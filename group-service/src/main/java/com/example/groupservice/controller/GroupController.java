package com.example.groupservice.controller;

import com.example.groupservice.dto.AddMemberRequest;
import com.example.groupservice.dto.CreateGroupRequest;
import com.example.groupservice.model.Group;
import com.example.groupservice.repository.GroupRepository;
import com.example.groupservice.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.groupservice.model.Group;
import java.util.List;

@RestController
@RequestMapping("/api/groups")
public class GroupController {

    @Autowired
    private GroupService groupService;

    @Autowired
    private GroupRepository groupRepository;

    @PostMapping("/create")
    public ResponseEntity<Group> createNewGroup(
            @RequestBody CreateGroupRequest request,
            // 从网关转发的请求头中获取当前登录用户的ID
            @RequestHeader("X-Authenticated-User-Id") Long userId
    ) {
        Group createdGroup = groupService.createGroup(request, userId);
        return new ResponseEntity<>(createdGroup, HttpStatus.CREATED);
    }

    @GetMapping("/user/{userId}")
    public List<Group> getGroupsForUser(@PathVariable Long userId) {
        return groupService.getGroupsForUser(userId);
    }

    @GetMapping("/{groupId}/members/{userId}/exists")
    public boolean isUserMember(@PathVariable Long groupId, @PathVariable Long userId) {
        return groupService.isUserMemberOfGroup(groupId, userId);
    }

    @PostMapping("/{groupId}/members")
    public ResponseEntity<?> addMember(
            @PathVariable Long groupId,
            @RequestBody AddMemberRequest request,
            @RequestHeader("X-Authenticated-User-Id") Long inviterId) {
        try {
            groupService.addMember(groupId, request.getUserId(), inviterId);
            return ResponseEntity.ok().body("Member added successfully.");
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/{groupId}/members")
    public ResponseEntity<List<Long>> getGroupMembers(@PathVariable Long groupId) {
        List<Long> memberIds = groupService.getMemberIdsByGroupId(groupId);
        return ResponseEntity.ok(memberIds);
    }

    @DeleteMapping("/{groupId}/members/{userId}")
    public ResponseEntity<?> removeMember(
            @PathVariable Long groupId,
            @PathVariable("userId") Long userIdToRemove, // 将路径变量重命名为userIdToRemove以示清晰
            @RequestHeader("X-Authenticated-User-Id") Long removerId) {

        try {
            groupService.removeMember(groupId, userIdToRemove, removerId);
            return ResponseEntity.ok().body("Member removed successfully.");
        } catch (SecurityException e) {
            // 如果不是群主操作，返回403 Forbidden
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            // 如果试图移除群主，返回400 Bad Request
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            // 其他未知错误
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    @GetMapping("/{groupId}")
    public ResponseEntity<Group> getGroupById(@PathVariable Long groupId) {
        // 利用JPA Repository自带的findById方法
        return groupRepository.findById(groupId)
                .map(ResponseEntity::ok) // 如果找到了，返回 200 OK 和群组信息
                .orElse(ResponseEntity.notFound().build()); // 如果没找到，返回 404 Not Found
    }
}
