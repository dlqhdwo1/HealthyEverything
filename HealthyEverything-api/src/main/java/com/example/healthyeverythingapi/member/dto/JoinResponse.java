package com.example.healthyeverythingapi.member.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class JoinResponse {

    private boolean success;
    private Data data;
    private Tokens tokens;
    private Error error;

    private JoinResponse(boolean success, Data data, Tokens tokens, Error error) {
        this.success = success;
        this.data = data;
        this.tokens = tokens;
        this.error = error;
    }

    public static JoinResponse success(String userId, String phoneNumber, Tokens tokens) {
        return new JoinResponse(
                true,
                new Data(userId, phoneNumber),
                tokens,
                null
        );
    }

    public static JoinResponse error(String code, String message) {
        return new JoinResponse(
                false,
                null,
                null,
                new Error(code, message)
        );
    }

    @Getter
    @NoArgsConstructor
    public static class Data {
        @JsonProperty("UserId")
        private String userId;
        private String phoneNumber;

        public Data(String userId, String phoneNumber) {
            this.userId = userId;
            this.phoneNumber = phoneNumber;
        }
    }

    @Getter
    @NoArgsConstructor
    public static class Tokens {
        private String accessToken;
        private String refreshToken;
        private long accessTokenExpiresIn;
        private long refreshTokenExpiresIn;

        public Tokens(String accessToken, String refreshToken, long accessTokenExpiresIn, long refreshTokenExpiresIn) {
            this.accessToken = accessToken;
            this.refreshToken = refreshToken;
            this.accessTokenExpiresIn = accessTokenExpiresIn;
            this.refreshTokenExpiresIn = refreshTokenExpiresIn;
        }
    }

    @Getter
    @NoArgsConstructor
    public static class Error {
        private String code;
        private String message;

        public Error(String code, String message) {
            this.code = code;
            this.message = message;
        }
    }
}
