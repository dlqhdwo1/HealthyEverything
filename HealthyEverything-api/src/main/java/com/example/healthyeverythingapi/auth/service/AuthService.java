package com.example.healthyeverythingapi.auth.service;

import com.example.healthyeverythingapi.auth.dto.AuthResponses;
import com.example.healthyeverythingapi.auth.dto.LoginRequest;
import com.example.healthyeverythingapi.auth.jwt.JwtTokenProvider;
import com.example.healthyeverythingapi.common.exception.DuplicateEmailException;
import com.example.healthyeverythingapi.common.exception.InvalidCredentialsException;
import com.example.healthyeverythingapi.member.dto.JoinRequest;
import com.example.healthyeverythingapi.user.domain.User;
import com.example.healthyeverythingapi.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public AuthResponses.SignupResponse signup(JoinRequest req) {
        if (userRepository.existsByEmail(req.getUserid())) {
            throw new DuplicateEmailException();
        }

        User saved = userRepository.save(User.builder()
                .email(req.getUserid())
                .passwordHash(passwordEncoder.encode(req.getPassword()))
                .name(req.getName())
                .build());

        return new AuthResponses.SignupResponse(saved.getId(), saved.getEmail(), saved.getName());
    }

    @Transactional(readOnly = true)
    public AuthResponses.LoginResponse login(LoginRequest req) {
        User user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(req.getPassword(), user.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }

        // JWT 토큰 생성
        String accessToken = jwtTokenProvider.createAccessToken(user.getId(), user.getEmail());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getId(), user.getEmail());

        return AuthResponses.LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .accessTokenExpiresIn(jwtTokenProvider.getAccessTokenValidityInSeconds())
                .refreshTokenExpiresIn(jwtTokenProvider.getRefreshTokenValidityInSeconds())
                .user(AuthResponses.LoginResponse.UserInfo.builder()
                        .id(user.getId())
                        .email(user.getEmail())
                        .name(user.getName())
                        .build())
                .build();
    }

    @Transactional(readOnly = true)
    public AuthResponses.LoginResponse refreshToken(String refreshToken) {
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new InvalidCredentialsException("유효하지 않은 리프레시 토큰입니다.");
        }

        String email = jwtTokenProvider.getEmailFromToken(refreshToken);
        Long userId = jwtTokenProvider.getUserIdFromToken(refreshToken);

        User user = userRepository.findByEmail(email)
                .orElseThrow(InvalidCredentialsException::new);

        // 새 토큰 발급
        String newAccessToken = jwtTokenProvider.createAccessToken(userId, email);
        String newRefreshToken = jwtTokenProvider.createRefreshToken(userId, email);

        return AuthResponses.LoginResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .accessTokenExpiresIn(jwtTokenProvider.getAccessTokenValidityInSeconds())
                .refreshTokenExpiresIn(jwtTokenProvider.getRefreshTokenValidityInSeconds())
                .user(AuthResponses.LoginResponse.UserInfo.builder()
                        .id(user.getId())
                        .email(user.getEmail())
                        .name(user.getName())
                        .build())
                .build();
    }
}
