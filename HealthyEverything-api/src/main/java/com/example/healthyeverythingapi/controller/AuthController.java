package com.example.healthyeverythingapi.controller;

import com.example.healthyeverythingapi.dto.LoginRequest;
import com.example.healthyeverythingapi.dto.TokenResponse;
import com.example.healthyeverythingapi.exception.InvalidCredentialsException;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @PostMapping("/login")
    public TokenResponse login(@Valid @RequestBody LoginRequest request) {

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
}