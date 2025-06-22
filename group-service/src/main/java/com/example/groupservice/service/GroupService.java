package com.example.groupservice.service;

import com.example.groupservice.dto.CreateGroupRequest;
import com.example.groupservice.dto.GroupMemberDTO;
import com.example.groupservice.model.Group;
import java.util.List;

public interface GroupService {

    List<Long> getMemberIdsByGroupId(Long groupId);

    Group createGroup(CreateGroupRequest request, Long ownerId);

    List<Group> getGroupsForUser(Long userId);

    boolean isUserMemberOfGroup(Long groupId, Long userId);

    void addMember(Long groupId, Long userIdToAdd, Long inviterId);

    void removeMember(Long groupId, Long userIdToRemove, Long removerId);

    // 【修正】将这个新方法声明移动到接口的大括号内部
    List<GroupMemberDTO> getGroupMembersWithDetails(Long groupId);
}
