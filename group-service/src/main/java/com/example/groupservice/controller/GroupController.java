package com.example.groupservice.controller;

<<<<<<< HEAD
import com.example.groupservice.dto.CreateGroupRequest;
import com.example.groupservice.model.Group;
import com.example.groupservice.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
=======
import com.example.groupservice.model.Group;
import com.example.groupservice.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List; // 确保导入
import org.springframework.web.bind.annotation.GetMapping; // 确保导入
import org.springframework.web.bind.annotation.PathVariable; // 确保导入

>>>>>>> 1a87df0d7045169a8a3e9611973c7c556173448b

@RestController
@RequestMapping("/api/groups")
public class GroupController {

    @Autowired
    private GroupService groupService;

<<<<<<< HEAD
    @PostMapping("/create")
    public ResponseEntity<Group> createNewGroup(
            @RequestBody CreateGroupRequest request,
            // 从网关转发的请求头中获取当前登录用户的ID
            @RequestHeader("X-Authenticated-User-Id") Long userId
    ) {
        Group createdGroup = groupService.createGroup(request, userId);
        return new ResponseEntity<>(createdGroup, HttpStatus.CREATED);
=======
    @PostMapping
    public Group createGroup(@RequestBody Group group) {
        return groupService.createGroup(group);
>>>>>>> 1a87df0d7045169a8a3e9611973c7c556173448b
    }

    @GetMapping("/user/{userId}")
    public List<Group> getGroupsForUser(@PathVariable Long userId) {
        return groupService.getGroupsForUser(userId);
    }

    @GetMapping("/{groupId}/members/{userId}/exists")
    public boolean isUserMember(@PathVariable Long groupId, @PathVariable Long userId) {
        return groupService.isUserMemberOfGroup(groupId, userId);
    }
<<<<<<< HEAD

    // --- 这里是我们的修改 ---
    // 我们将 @PostMapping 改为了 @GetMapping，这样就可以直接通过浏览器地址栏来调用了。
    // 注意：在正式项目中，修改数据的操作（如添加成员）仍然推荐使用 POST。
    @GetMapping("/{groupId}/members/{userId}")
    public void addMember(@PathVariable Long groupId, @PathVariable Long userId) {
        groupService.addMember(groupId, userId);
    }
=======
>>>>>>> 1a87df0d7045169a8a3e9611973c7c556173448b
}
