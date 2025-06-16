package com.example.groupservice.dto;

import lombok.Data;

@Data
public class CreateGroupRequest {
    private String name;
    private String description;
}
