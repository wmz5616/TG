package com.example.groupservice.service;

import com.example.groupservice.dto.CreateGroupRequest;
import com.example.groupservice.model.Group;
import java.util.List;
import com.example.groupservice.dto.CreateGroupRequest;

public interface GroupService {
    List<Long> getMemberIdsByGroupId(Long groupId);
    Group createGroup(CreateGroupRequest request, Long ownerId);
    List<Group> getGroupsForUser(Long userId);
    boolean isUserMemberOfGroup(Long groupId, Long userId);
    void addMember(Long groupId, Long userId);

    void addMember(Long groupId, Long userIdToAdd, Long inviterId);
    void removeMember(Long groupId, Long userIdToRemove, Long removerId);
}
