package com.example.healthyeverythingapi.member.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "chat_rooms")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long partnerId;

    @Column(nullable = false, length = 100)
    private String partnerName;

    @Column(length = 255)
    private String partnerProfileImageUrl;

    @Column(length = 500)
    private String lastMessage;

    @Column(length = 20)
    private String lastMessageAt;

    @Column
    private int unreadCount;

    // 마지막 메시지 업데이트
    public void updateLastMessage(String message, String messageAt) {
        this.lastMessage = message;
        this.lastMessageAt = messageAt;
    }

    // 읽지 않은 메시지 증가
    public void incrementUnreadCount() {
        this.unreadCount++;
    }

    // 읽음 처리
    public void resetUnreadCount() {
        this.unreadCount = 0;
    }
}
