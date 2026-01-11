package com.example.healthyeverythingapi.chat.service;

import com.example.healthyeverythingapi.chat.domain.ChatMessage;
import com.example.healthyeverythingapi.chat.dto.ChatMessageRequest;
import com.example.healthyeverythingapi.chat.dto.ChatMessageResponse;
import com.example.healthyeverythingapi.chat.dto.ChatUserResponse;
import com.example.healthyeverythingapi.member.domain.ChatRoom;
import com.example.healthyeverythingapi.member.service.ChatRoomService;
import com.example.healthyeverythingapi.user.domain.User;
import com.example.healthyeverythingapi.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatService {

    private final ChatMessageService chatMessageService;
    private final ChatRoomService chatRoomService;
    private final UserService userService;

    // 메시지 전송
    @Transactional
    public ChatMessageResponse sendMessage(Long senderId, ChatMessageRequest request) {
        User sender = userService.findById(senderId);
        User receiver = userService.findById(request.getReceiverId());

        // roomId는 두 유저 ID의 조합으로 생성 (작은 ID가 앞)
        Long roomId = generateRoomId(senderId, request.getReceiverId());

        ChatMessage message = ChatMessage.builder()
                .roomId(roomId)
                .senderId(senderId)
                .receiverId(request.getReceiverId())
                .content(request.getContent())
                .build();

        ChatMessage savedMessage = chatMessageService.save(message);

        // ChatRoom 업데이트 (양방향)
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        updateOrCreateChatRoom(senderId, receiver, request.getContent(), now, false);
        updateOrCreateChatRoom(request.getReceiverId(), sender, request.getContent(), now, true);

        return ChatMessageResponse.from(savedMessage, sender.getName());
    }

    // ChatRoom 생성 또는 업데이트
    private void updateOrCreateChatRoom(Long userId, User partner, String lastMessage, String lastMessageAt, boolean incrementUnread) {
        ChatRoom chatRoom = chatRoomService.findByUserIdAndPartnerId(userId, partner.getId())
                .orElse(null);

        if (chatRoom == null) {
            // 새로 생성
            chatRoom = ChatRoom.builder()
                    .userId(userId)
                    .partnerId(partner.getId())
                    .partnerName(partner.getName())
                    .lastMessage(lastMessage)
                    .lastMessageAt(lastMessageAt)
                    .unreadCount(incrementUnread ? 1 : 0)
                    .build();
        } else {
            // 업데이트
            chatRoom.updateLastMessage(lastMessage, lastMessageAt);
            if (incrementUnread) {
                chatRoom.incrementUnreadCount();
            }
        }

        chatRoomService.save(chatRoom);
    }

    // 두 유저 간의 채팅 메시지 조회
    public List<ChatMessageResponse> getMessages(Long userId, Long partnerId) {
        Long roomId = generateRoomId(userId, partnerId);

        List<ChatMessage> messages = chatMessageService.findByRoomIdOrderByCreatedAtAsc(roomId);

        return messages.stream()
                .map(msg -> {
                    User sender = userService.findById(msg.getSenderId());
                    return ChatMessageResponse.from(msg, sender.getName());
                })
                .collect(Collectors.toList());
    }

    // 채팅 가능한 유저 목록 조회 (테스트용 - 본인 제외 전체)
    public List<ChatUserResponse> getChatUsers(Long currentUserId) {
        return userService.findAll().stream()
                .filter(user -> !user.getId().equals(currentUserId))
                .map(ChatUserResponse::from)
                .collect(Collectors.toList());
    }

    // roomId 생성 (두 유저 ID 조합, 작은 ID가 앞)
    private Long generateRoomId(Long userId1, Long userId2) {
        long min = Math.min(userId1, userId2);
        long max = Math.max(userId1, userId2);
        return min * 1000000 + max;
    }
}