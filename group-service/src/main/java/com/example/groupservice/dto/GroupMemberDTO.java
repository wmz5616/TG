package com.example.groupservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupMemberDTO {
    private Long id;
    private String username;
    // 如果需要，未来还可以添加头像等信息
}
