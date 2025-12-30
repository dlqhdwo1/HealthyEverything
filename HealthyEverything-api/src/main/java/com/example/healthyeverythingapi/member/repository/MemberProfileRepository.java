package com.example.healthyeverythingapi.member.repository;

import com.example.healthyeverythingapi.member.domain.MemberProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberProfileRepository extends JpaRepository<MemberProfile, Long> {

    Optional<MemberProfile> findByUserId(Long userId);
}
