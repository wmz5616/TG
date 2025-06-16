package com.example.groupservice.service;

<<<<<<< HEAD
import com.example.groupservice.dto.CreateGroupRequest;
=======
>>>>>>> 1a87df0d7045169a8a3e9611973c7c556173448b
import com.example.groupservice.model.Group;
import com.example.groupservice.model.GroupMember;
import com.example.groupservice.repository.GroupMemberRepository;
import com.example.groupservice.repository.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List; // 确保导入
import java.util.stream.Collectors;

<<<<<<< HEAD

=======
>>>>>>> 1a87df0d7045169a8a3e9611973c7c556173448b
@Service
public class GroupServiceImpl implements GroupService {

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private GroupMemberRepository groupMemberRepository;

<<<<<<< HEAD
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
=======
    @Override
    @Transactional // 2. 关键注解！
    public Group createGroup(Group group) {
        // 步骤一：保存群组信息到数据库，这样我们就能获得一个带ID的group对象
        Group savedGroup = groupRepository.save(group);

        // 步骤二：创建一个新的 GroupMember 对象，将群主添加为第一个成员
        GroupMember ownerAsMember = new GroupMember();
        ownerAsMember.setGroupId(savedGroup.getId());
        ownerAsMember.setUserId(savedGroup.getOwnerId());

        // 步骤三：保存群成员信息到数据库
>>>>>>> 1a87df0d7045169a8a3e9611973c7c556173448b
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
<<<<<<< HEAD

    @Override
    public void addMember(Long groupId, Long userId) {
        // 1. 检查用户是否已经是成员，避免重复添加
        boolean isAlreadyMember = groupMemberRepository.existsByGroupIdAndUserId(groupId, userId);
        if (isAlreadyMember) {
            // 如果已经是成员，可以什么都不做，或者抛出一个异常
            System.out.println("用户 " + userId + " 已经是群 " + groupId + " 的成员了。");
            return;
        }

        // 2. 如果不是成员，则创建新的成员关系并保存
        GroupMember newMember = new GroupMember();
        newMember.setGroupId(groupId);
        newMember.setUserId(userId);
        groupMemberRepository.save(newMember);
        System.out.println("成功将用户 " + userId + " 添加到群 " + groupId);
    }
=======
>>>>>>> 1a87df0d7045169a8a3e9611973c7c556173448b
}
