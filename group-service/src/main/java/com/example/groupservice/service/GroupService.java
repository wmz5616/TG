package com.example.groupservice.service;

<<<<<<< HEAD
import com.example.groupservice.dto.CreateGroupRequest;
import com.example.groupservice.model.Group;
import java.util.List;
import com.example.groupservice.dto.CreateGroupRequest;

public interface GroupService {
    Group createGroup(CreateGroupRequest request, Long ownerId);
    List<Group> getGroupsForUser(Long userId);
    boolean isUserMemberOfGroup(Long groupId, Long userId);
    void addMember(Long groupId, Long userId);

=======
import com.example.groupservice.model.Group;
import java.util.List;

public interface GroupService {
    Group createGroup(Group group);
    List<Group> getGroupsForUser(Long userId);
    boolean isUserMemberOfGroup(Long groupId, Long userId);
>>>>>>> 1a87df0d7045169a8a3e9611973c7c556173448b
}
