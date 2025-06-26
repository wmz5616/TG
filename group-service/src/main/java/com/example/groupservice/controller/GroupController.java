package com.example.groupservice.controller;

import com.example.groupservice.dto.AddMemberRequest;
import com.example.groupservice.dto.CreateGroupRequest;
import com.example.groupservice.dto.GroupMemberDTO;
import com.example.groupservice.model.Group;
import com.example.groupservice.repository.GroupRepository;
import com.example.groupservice.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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
            @PathVariable("userId") Long userIdToRemove,
            @RequestHeader("X-Authenticated-User-Id") Long removerId) {
        try {
            groupService.removeMember(groupId, userIdToRemove, removerId);
            return ResponseEntity.ok().body("Member removed successfully.");
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    @GetMapping("/{groupId}")
    public ResponseEntity<Group> getGroupById(@PathVariable Long groupId) {
        return groupRepository.findById(groupId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    @GetMapping("/{groupId}/members/details")
    public ResponseEntity<List<GroupMemberDTO>> getGroupMembersDetails(@PathVariable Long groupId) {
        List<GroupMemberDTO> members = groupService.getGroupMembersWithDetails(groupId);
        return ResponseEntity.ok(members);
    }
    @GetMapping("/user/{userId}/ids")
    public List<Long> getGroupIdsForUser(@PathVariable Long userId) {
        return groupService.getGroupIdsByUserId(userId);
    }
    // 【【新增接口2】】 供 message-service 调用
    @GetMapping("/batch")
    public List<Group> getGroupsByIds(@RequestParam("ids") List<Long> ids) {
        return groupService.getGroupsByIds(ids);
    }
    @GetMapping("/search")
    public ResponseEntity<List<Group>> searchGroups(@RequestParam("name") String name) {
        return ResponseEntity.ok(groupService.searchGroups(name));
    }
}
