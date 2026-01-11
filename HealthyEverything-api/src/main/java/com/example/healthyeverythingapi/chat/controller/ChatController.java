package com.example.healthyeverythingapi.chat.controller;

import com.example.healthyeverythingapi.chat.dto.ChatMessageRequest;
import com.example.healthyeverythingapi.chat.dto.ChatMessageResponse;
import com.example.healthyeverythingapi.chat.dto.ChatUserResponse;
import com.example.healthyeverythingapi.chat.service.ChatService;
import com.example.healthyeverythingapi.user.domain.User;
import com.example.healthyeverythingapi.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final UserService userService;

    // 채팅 페이지
    @GetMapping("/chat")
    public String chatPage() {
        return "chat";
    }

    // 채팅 가능한 유저 목록 API
    @GetMapping("/api/chat/users")
    @ResponseBody
    public ResponseEntity<List<ChatUserResponse>> getChatUsers(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = getUserId(userDetails);
        List<ChatUserResponse> users = chatService.getChatUsers(userId);
        return ResponseEntity.ok(users);
    }

    // 메시지 전송 API
    @PostMapping("/api/chat/messages")
    @ResponseBody
    public ResponseEntity<ChatMessageResponse> sendMessage(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody ChatMessageRequest request) {
        Long userId = getUserId(userDetails);
        ChatMessageResponse response = chatService.sendMessage(userId, request);
        return ResponseEntity.ok(response);
    }

    // 메시지 조회 API
    @GetMapping("/api/chat/messages/{partnerId}")
    @ResponseBody
    public ResponseEntity<List<ChatMessageResponse>> getMessages(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long partnerId) {
        Long userId = getUserId(userDetails);
        List<ChatMessageResponse> messages = chatService.getMessages(userId, partnerId);
        return ResponseEntity.ok(messages);
    }

    private Long getUserId(UserDetails userDetails) {
        String email = userDetails.getUsername();
        User user = userService.findByEmail(email);
        return user.getId();
    }
}