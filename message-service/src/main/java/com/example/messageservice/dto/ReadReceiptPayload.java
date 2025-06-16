package com.example.messageservice.dto;

import lombok.Data;
import java.util.List;

@Data
public class ReadReceiptPayload {
    private List<Long> messageIds;
    private Long readerId;
}
