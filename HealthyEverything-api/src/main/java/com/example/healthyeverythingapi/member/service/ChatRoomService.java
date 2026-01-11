package com.example.healthyeverythingapi.member.service;

import com.example.healthyeverythingapi.member.domain.ChatRoom;
import com.example.healthyeverythingapi.member.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;

    public List<ChatRoom> findByUserId(Long userId) {
        return chatRoomRepository.findByUserId(userId);
    }

    public Optional<ChatRoom> findByUserIdAndPartnerId(Long userId, Long partnerId) {
        return chatRoomRepository.findByUserIdAndPartnerId(userId, partnerId);
    }

    @Transactional
    public ChatRoom save(ChatRoom chatRoom) {
        return chatRoomRepository.save(chatRoom);
    }
}