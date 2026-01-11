package com.example.healthyeverythingapi.member.service;

import com.example.healthyeverythingapi.member.domain.Alarm;
import com.example.healthyeverythingapi.member.repository.AlarmRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AlarmService {

    private final AlarmRepository alarmRepository;

    public List<Alarm> findByUserId(Long userId) {
        return alarmRepository.findByUserId(userId);
    }

    public Alarm findById(Long id) {
        return alarmRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ALARM_NOT_FOUND"));
    }
}
