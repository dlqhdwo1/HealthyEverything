package com.example.healthyeverythingapi.member.service;

import com.example.healthyeverythingapi.member.domain.MemberProfile;
import com.example.healthyeverythingapi.member.repository.MemberProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberProfileService {

    private final MemberProfileRepository memberProfileRepository;

    public MemberProfile findByUserId(Long userId) {
        return memberProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("MEMBER_NOT_FOUND"));
    }
}