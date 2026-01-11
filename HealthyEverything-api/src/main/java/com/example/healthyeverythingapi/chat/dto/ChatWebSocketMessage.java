package com.example.healthyeverythingapi.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatWebSocketMessage {
    private Long senderId;
    private Long receiverId;
    private String senderName;
    private String content;
    private String createdAt;
}
