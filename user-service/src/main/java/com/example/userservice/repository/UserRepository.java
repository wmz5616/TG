<<<<<<< HEAD
package com.example.userservice.repository;

import com.example.userservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional; // 导入 Optional
import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    // 【新增】根据用户名查找用户
    // Spring Data JPA 会自动根据方法名生成查询
    // Optional<User> 表示这个查询可能找到一个用户，也可能什么都没找到
    Optional<User> findByUsername(String username);
    List<User> findByUsernameContainingIgnoreCase(String username);
=======
package com.example.userservice.repository; // 检查这行包名是否正确

import com.example.userservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

// 检查这里必须是 public interface，而不是 class
public interface UserRepository extends JpaRepository<User, Long> {

>>>>>>> 1a87df0d7045169a8a3e9611973c7c556173448b
}
