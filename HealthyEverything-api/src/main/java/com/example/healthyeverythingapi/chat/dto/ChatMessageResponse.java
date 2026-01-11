package com.example.healthyeverythingapi.chat.dto;

import com.example.healthyeverythingapi.chat.domain.ChatMessage;
import lombok.Builder;
import lombok.Getter;

import java.time.format.DateTimeFormatter;

@Getter
@Builder
public class ChatMessageResponse {
    private Long id;
    private Long senderId;
    private Long receiverId;
    private String senderName;
    private String content;
    private String createdAt;

    public static ChatMessageResponse from(ChatMessage message, String senderName) {
        return ChatMessageResponse.builder()
                .id(message.getId())
                .senderId(message.getSenderId())
                .receiverId(message.getReceiverId())
                .senderName(senderName)
                .content(message.getContent())
                .createdAt(message.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .build();
    }
}