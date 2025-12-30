package com.example.healthyeverythingapi.member.repository;

import com.example.healthyeverythingapi.member.domain.Alarm;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlarmRepository extends JpaRepository<Alarm, Long> {

    List<Alarm> findByUserId(Long userId);
}
