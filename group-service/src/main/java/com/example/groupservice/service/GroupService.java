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
    List<GroupMemberDTO> getGroupMembersWithDetails(Long groupId);
    List<Long> getGroupIdsByUserId(Long userId);
    List<Group> getGroupsByIds(List<Long> groupIds);
    List<Group> searchGroups(String name);
}
