package com.example.healthyeverythingapi.auth.service;

import com.example.healthyeverythingapi.auth.dto.AuthResponses;
import com.example.healthyeverythingapi.auth.dto.LoginRequest;
import com.example.healthyeverythingapi.member.dto.JoinRequest;
import com.example.healthyeverythingapi.member.dto.JoinResponse;
import com.example.healthyeverythingapi.user.domain.User;
import com.example.healthyeverythingapi.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public AuthResponses.SignupResponse signup(JoinRequest req) {
        if (userRepository.existsByEmail(req.getUserid())) {
            throw new IllegalArgumentException("DUPLICATE_EMAIL");
        }

        User saved = userRepository.save(User.builder()
                .email(req.getUserid())
                .passwordHash(passwordEncoder.encode(req.getPassword()))
                .name(req.getName())
                .build());

        return new AuthResponses.SignupResponse(saved.getId(), saved.getEmail(),saved.getName());
    }

    @Transactional(readOnly = true)
    public AuthResponses.LoginResponse login(LoginRequest req) {
        User user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("INVALID_CREDENTIALS")); // orElseThrow : optional에 값이 없을때 즉시 반환

        if (!passwordEncoder.matches(req.getPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("INVALID_CREDENTIALS");
        }

        // 테스트용 간단 토큰(실서비스는 JWT)
        return new AuthResponses.LoginResponse("test-token-" + UUID.randomUUID());
    }

}
