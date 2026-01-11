package com.example.healthyeverythingapi.chat.controller;

import com.example.healthyeverythingapi.chat.domain.ChatMessage;
import com.example.healthyeverythingapi.chat.dto.ChatMessageRequest;
import com.example.healthyeverythingapi.chat.dto.ChatWebSocketMessage;
import com.example.healthyeverythingapi.chat.service.ChatMessageService;
import com.example.healthyeverythingapi.member.domain.ChatRoom;
import com.example.healthyeverythingapi.member.service.ChatRoomService;
import com.example.healthyeverythingapi.user.domain.User;
import com.example.healthyeverythingapi.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessageService chatMessageService;
    private final ChatRoomService chatRoomService;
    private final UserService userService;

    @MessageMapping("/chat.send")
    public void sendMessage(@Payload ChatWebSocketMessage message) {
        // 발신자 정보 조회
        User sender = userService.findById(message.getSenderId());
        User receiver = userService.findById(message.getReceiverId());

        // roomId 생성
        Long roomId = generateRoomId(message.getSenderId(), message.getReceiverId());

        // DB에 메시지 저장
        ChatMessage chatMessage = ChatMessage.builder()
                .roomId(roomId)
                .senderId(message.getSenderId())
                .receiverId(message.getReceiverId())
                .content(message.getContent())
                .build();

        ChatMessage savedMessage = chatMessageService.save(chatMessage);

        // ChatRoom 업데이트
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        updateOrCreateChatRoom(message.getSenderId(), receiver, message.getContent(), now, false);
        updateOrCreateChatRoom(message.getReceiverId(), sender, message.getContent(), now, true);

        // 응답 메시지 생성
        ChatWebSocketMessage response = ChatWebSocketMessage.builder()
                .senderId(message.getSenderId())
                .receiverId(message.getReceiverId())
                .senderName(sender.getName())
                .content(message.getContent())
                .createdAt(savedMessage.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .build();

        // 발신자와 수신자 모두에게 메시지 전송
        messagingTemplate.convertAndSend("/topic/chat/" + message.getSenderId(), response);
        messagingTemplate.convertAndSend("/topic/chat/" + message.getReceiverId(), response);
    }

    private void updateOrCreateChatRoom(Long userId, User partner, String lastMessage, String lastMessageAt, boolean incrementUnread) {
        ChatRoom chatRoom = chatRoomService.findByUserIdAndPartnerId(userId, partner.getId())
                .orElse(null);

        if (chatRoom == null) {
            chatRoom = ChatRoom.builder()
                    .userId(userId)
                    .partnerId(partner.getId())
                    .partnerName(partner.getName())
                    .lastMessage(lastMessage)
                    .lastMessageAt(lastMessageAt)
                    .unreadCount(incrementUnread ? 1 : 0)
                    .build();
        } else {
            chatRoom.updateLastMessage(lastMessage, lastMessageAt);
            if (incrementUnread) {
                chatRoom.incrementUnreadCount();
            }
        }

        chatRoomService.save(chatRoom);
    }

    private Long generateRoomId(Long userId1, Long userId2) {
        long min = Math.min(userId1, userId2);
        long max = Math.max(userId1, userId2);
        return min * 1000000 + max;
    }
}
