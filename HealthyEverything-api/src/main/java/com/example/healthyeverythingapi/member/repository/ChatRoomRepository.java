package com.example.healthyeverythingapi.member.repository;

import com.example.healthyeverythingapi.member.domain.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    List<ChatRoom> findByUserId(Long userId);

    Optional<ChatRoom> findByUserIdAndPartnerId(Long userId, Long partnerId);
}
