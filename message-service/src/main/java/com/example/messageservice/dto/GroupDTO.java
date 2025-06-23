package com.example.messageservice.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class GroupDTO {
    private Long id;
    private String name;
    // 我们暂时只需要群组的ID和名字，未来可以按需添加头像等字段
}
