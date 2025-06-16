package com.example.messageservice.dto;

import com.example.messageservice.model.MessageStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageStatusUpdate {
    private Long messageId;
    private MessageStatus status;
}
