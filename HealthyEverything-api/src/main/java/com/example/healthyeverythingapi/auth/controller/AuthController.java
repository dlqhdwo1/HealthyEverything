package com.example.healthyeverythingapi.auth.controller;

import com.example.healthyeverythingapi.auth.dto.LoginRequest;
import com.example.healthyeverythingapi.auth.dto.RefreshTokenRequest;
import com.example.healthyeverythingapi.member.dto.TokenResponse;
import com.example.healthyeverythingapi.exception.InvalidCredentialsException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @PostMapping("/login")
    public TokenResponse login(@Valid @RequestBody LoginRequest request) {


        if("".equals(request.getEmail()) || "".equals(request.getPassword())) {
            throw new InvalidCredentialsException();
        }


        if ("wrong!".equals(request.getPassword())) {
            throw new InvalidCredentialsException();
        }

        return new TokenResponse(
                "access.jwt.token",
                "refresh.jwt.token",
                "Bearer",
                3600L
        );
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