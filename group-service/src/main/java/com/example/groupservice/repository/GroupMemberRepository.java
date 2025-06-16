package com.example.groupservice.repository;

import com.example.groupservice.model.GroupMember;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List; // 确保导入

public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {

    // 根据方法名自动生成查询：查找所有 userId 匹配的记录
    List<GroupMember> findByUserId(Long userId);
    boolean existsByGroupIdAndUserId(Long groupId, Long userId);
}
