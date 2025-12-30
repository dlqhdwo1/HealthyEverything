package com.example.healthyeverythingapi.member.repository;

import com.example.healthyeverythingapi.member.domain.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    List<ChatRoom> findByUserId(Long userId);
}
