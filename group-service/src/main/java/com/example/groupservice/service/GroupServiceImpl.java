package com.example.groupservice.service;

import com.example.groupservice.dto.CreateGroupRequest;
import com.example.groupservice.model.Group;
import com.example.groupservice.model.GroupMember;
import com.example.groupservice.repository.GroupMemberRepository;
import com.example.groupservice.repository.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List; // 确保导入
import java.util.stream.Collectors;


@Service
public class GroupServiceImpl implements GroupService {

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private GroupMemberRepository groupMemberRepository;

    // 【新增】实现新的创建群组方法
    @Override
    @Transactional
    public Group createGroup(CreateGroupRequest request, Long ownerId) {
        // 1. 创建并设置 Group 实体
        Group newGroup = new Group();
        newGroup.setName(request.getName());
        newGroup.setDescription(request.getDescription());
        newGroup.setOwnerId(ownerId);

        // 2. 保存 Group 实体到数据库，以获取生成的群组ID
        Group savedGroup = groupRepository.save(newGroup);

        // 3. 将群主作为第一个成员添加到群组
        GroupMember ownerAsMember = new GroupMember();
        ownerAsMember.setGroupId(savedGroup.getId());
        ownerAsMember.setUserId(ownerId);
        groupMemberRepository.save(ownerAsMember);

        return savedGroup;
    }

    @Override
    public List<Group> getGroupsForUser(Long userId) {
        // 步骤一：根据 userId 找到所有成员关系记录
        List<GroupMember> memberships = groupMemberRepository.findByUserId(userId);

        // 步骤二：从成员关系中，提取出所有的 groupId
        // 我们使用 Java Stream API 来非常方便地完成这个转换
        List<Long> groupIds = memberships.stream()
                .map(GroupMember::getGroupId)
                .collect(Collectors.toList());

        // 如果用户没有加入任何群组，直接返回一个空列表
        if (groupIds.isEmpty()) {
            return List.of(); // 返回一个不可变的空列表
        }

        // 步骤三：根据 groupId 列表，一次性查询出所有群组的详细信息
        return groupRepository.findAllById(groupIds);
    }

    @Override
    public boolean isUserMemberOfGroup(Long groupId, Long userId) {
        return groupMemberRepository.existsByGroupIdAndUserId(groupId, userId);
    }

    @Override
    public void addMember(Long groupId, Long userId) {

    }

    @Override
    @Transactional // 添加事务注解，保证操作的原子性
    public void addMember(Long groupId, Long userIdToAdd, Long inviterId) {
        // 1. 查找群组信息
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found with id: " + groupId));

        // 2. 【【安全校验】】检查发起邀请的人是否是群主
        if (!group.getOwnerId().equals(inviterId)) {
            // 如果不是群主，则抛出异常，阻止操作
            throw new SecurityException("Access Denied: Only the group owner can add members.");
        }

        // 3. 检查用户是否已经是成员，避免重复添加
        boolean isAlreadyMember = groupMemberRepository.existsByGroupIdAndUserId(groupId, userIdToAdd);
        if (isAlreadyMember) {
            // 这里可以保持静默，或者抛出特定异常让前端知道“用户已在群里”
            System.out.println("User " + userIdToAdd + " is already a member of group " + groupId);
            return;
        }

        // 4. 如果校验通过，则创建新的成员关系并保存
        GroupMember newMember = new GroupMember();
        newMember.setGroupId(groupId);
        newMember.setUserId(userIdToAdd);
        groupMemberRepository.save(newMember);
        System.out.println("Successfully added user " + userIdToAdd + " to group " + groupId);
    }

    @Override
    public List<Long> getMemberIdsByGroupId(Long groupId) {
        // 1. 调用 repository 方法获取成员关系列表
        List<GroupMember> members = groupMemberRepository.findByGroupId(groupId);

        // 2. 使用 Java Stream API 从成员关系对象中提取出 userId，并收集成一个新的列表
        return members.stream()
                .map(GroupMember::getUserId)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void removeMember(Long groupId, Long userIdToRemove, Long removerId) {
        // 1. 查找群组信息
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found with id: " + groupId));

        // 2. 【安全校验】检查发起操作的人 (removerId) 是否是群主
        if (!group.getOwnerId().equals(removerId)) {
            throw new SecurityException("Access Denied: Only the group owner can remove members.");
        }

        // 3. 【业务逻辑校验】群主不能移除自己
        if (group.getOwnerId().equals(userIdToRemove)) {
            throw new IllegalArgumentException("Group owner cannot be removed.");
        }

        // 4. 执行删除操作
        groupMemberRepository.deleteByGroupIdAndUserId(groupId, userIdToRemove);
        System.out.println("Successfully removed user " + userIdToRemove + " from group " + groupId);
    }
}
