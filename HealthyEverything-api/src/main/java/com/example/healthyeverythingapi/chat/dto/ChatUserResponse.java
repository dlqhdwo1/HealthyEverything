package com.example.healthyeverythingapi.chat.dto;

import com.example.healthyeverythingapi.user.domain.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChatUserResponse {
    private Long id;
    private String name;
    private String email;

    public static ChatUserResponse from(User user) {
        return ChatUserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }
}