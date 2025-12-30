package com.example.healthyeverythingapi.auth.controller;

import com.example.healthyeverythingapi.auth.dto.AuthResponses;
import com.example.healthyeverythingapi.auth.dto.LoginRequest;
import com.example.healthyeverythingapi.auth.dto.RefreshTokenRequest;
import com.example.healthyeverythingapi.auth.service.AuthService;
import com.example.healthyeverythingapi.common.exception.InvalidCredentialsException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public AuthResponses.LoginResponse login(@Valid @RequestBody LoginRequest request) {
        try {
            return authService.login(request);
        } catch (IllegalArgumentException e) {
            throw new InvalidCredentialsException();
        }
    }

    @PostMapping("/token/refresh")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Object> reissue(@Valid @RequestBody RefreshTokenRequest request) {

        if ("invalid".equals(request.getRefreshToken())) {
            throw new RuntimeException("INVALID_REFRESH_TOKEN");
        }

        return Map.of(
                "success", true,
                "data", Map.of(
                        "email", "star5436@naver.com",
                        "name", "이정규"
                ),
                "tokens", Map.of(
                        "accessToken", "new_access_jwt...",
                        "refreshToken", "new_refresh_jwt...",
                        "accessTokenExpiresIn", 3600,
                        "refreshTokenExpiresIn", 2592000
                )
        );
    }
}