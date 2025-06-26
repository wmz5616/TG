package com.example.groupservice.repository;

import com.example.groupservice.model.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface GroupRepository extends JpaRepository<Group, Long> {
    List<Group> findByNameContainingIgnoreCase(String name);
}
