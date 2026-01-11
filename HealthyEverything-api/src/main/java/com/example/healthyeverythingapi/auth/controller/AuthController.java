package com.example.healthyeverythingapi.auth.controller;

import com.example.healthyeverythingapi.auth.dto.AuthResponses;
import com.example.healthyeverythingapi.auth.dto.LoginRequest;
import com.example.healthyeverythingapi.auth.dto.RefreshTokenRequest;
import com.example.healthyeverythingapi.auth.service.AuthService;
import com.example.healthyeverythingapi.member.dto.JoinRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponses.SignupResponse signup(@Valid @RequestBody JoinRequest request) {
        return authService.signup(request);
    }

    @PostMapping("/login")
    public AuthResponses.LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/token/refresh")
    @ResponseStatus(HttpStatus.OK)
    public AuthResponses.LoginResponse reissue(@Valid @RequestBody RefreshTokenRequest request) {
        return authService.refreshToken(request.getRefreshToken());
    }
}