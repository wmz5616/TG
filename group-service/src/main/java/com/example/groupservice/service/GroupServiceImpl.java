package com.example.groupservice.service;

import com.example.groupservice.client.UserClient; // 【新增】导入 UserClient
import com.example.groupservice.dto.CreateGroupRequest;
import com.example.groupservice.dto.GroupMemberDTO;
import com.example.groupservice.model.Group;
import com.example.groupservice.model.GroupMember;
import com.example.groupservice.repository.GroupMemberRepository;
import com.example.groupservice.repository.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GroupServiceImpl implements GroupService {

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private GroupMemberRepository groupMemberRepository;

    @Autowired // 【修正】注入 UserClient，这样我们才能调用其他服务
    private UserClient userClient;

    // ... (您其他的旧方法，例如 createGroup, getGroupsForUser 等，保持不变) ...

    @Override
    @Transactional
    public Group createGroup(CreateGroupRequest request, Long ownerId) {
        Group newGroup = new Group();
        newGroup.setName(request.getName());
        newGroup.setDescription(request.getDescription());
        newGroup.setOwnerId(ownerId);
        Group savedGroup = groupRepository.save(newGroup);
        GroupMember ownerAsMember = new GroupMember();
        ownerAsMember.setGroupId(savedGroup.getId());
        ownerAsMember.setUserId(ownerId);
        groupMemberRepository.save(ownerAsMember);
        return savedGroup;
    }

    @Override
    public List<Group> getGroupsForUser(Long userId) {
        List<GroupMember> memberships = groupMemberRepository.findByUserId(userId);
        List<Long> groupIds = memberships.stream()
                .map(GroupMember::getGroupId)
                .collect(Collectors.toList());
        if (groupIds.isEmpty()) {
            return List.of();
        }
        return groupRepository.findAllById(groupIds);
    }

    @Override
    public boolean isUserMemberOfGroup(Long groupId, Long userId) {
        return groupMemberRepository.existsByGroupIdAndUserId(groupId, userId);
    }

    @Override
    @Transactional
    public void addMember(Long groupId, Long userIdToAdd, Long inviterId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found with id: " + groupId));
        if (!group.getOwnerId().equals(inviterId)) {
            throw new SecurityException("Access Denied: Only the group owner can add members.");
        }
        boolean isAlreadyMember = groupMemberRepository.existsByGroupIdAndUserId(groupId, userIdToAdd);
        if (isAlreadyMember) {
            System.out.println("User " + userIdToAdd + " is already a member of group " + groupId);
            return;
        }
        GroupMember newMember = new GroupMember();
        newMember.setGroupId(groupId);
        newMember.setUserId(userIdToAdd);
        groupMemberRepository.save(newMember);
        System.out.println("Successfully added user " + userIdToAdd + " to group " + groupId);
    }

    @Override
    public List<Long> getMemberIdsByGroupId(Long groupId) {
        List<GroupMember> members = groupMemberRepository.findByGroupId(groupId);
        return members.stream()
                .map(GroupMember::getUserId)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void removeMember(Long groupId, Long userIdToRemove, Long removerId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found with id: " + groupId));
        if (!group.getOwnerId().equals(removerId)) {
            throw new SecurityException("Access Denied: Only the group owner can remove members.");
        }
        if (group.getOwnerId().equals(userIdToRemove)) {
            throw new IllegalArgumentException("Group owner cannot be removed.");
        }
        groupMemberRepository.deleteByGroupIdAndUserId(groupId, userIdToRemove);
        System.out.println("Successfully removed user " + userIdToRemove + " from group " + groupId);
    }


    // 【修正】为新方法加上 @Override 注解
    @Override
    public List<GroupMemberDTO> getGroupMembersWithDetails(Long groupId) {
        // 1. 获取成员ID列表 (调用已有的方法)
        List<Long> memberIds = this.getMemberIdsByGroupId(groupId);
        if (memberIds.isEmpty()) {
            return List.of(); // 如果群里没人，直接返回空列表
        }
        // 2. 通过 Feign Client 批量调用 user-service 获取用户信息
        return userClient.getUsersByIds(memberIds);
    }
}
