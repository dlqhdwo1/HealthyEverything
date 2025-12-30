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
}
