package com.example.healthyeverythingapi.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class AuthResponses {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SignupResponse {
        private Long userId;
        private String email;
        private String name;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginResponse {
        private String accessToken;
    }
}
