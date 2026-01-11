package com.example.healthyeverythingapi.chat.service;

import com.example.healthyeverythingapi.chat.domain.ChatMessage;
import com.example.healthyeverythingapi.chat.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;

    public List<ChatMessage> findByRoomIdOrderByCreatedAtAsc(Long roomId) {
        return chatMessageRepository.findByRoomIdOrderByCreatedAtAsc(roomId);
    }

    @Transactional
    public ChatMessage save(ChatMessage chatMessage) {

        return chatMessageRepository.save(chatMessage);
    }
}
