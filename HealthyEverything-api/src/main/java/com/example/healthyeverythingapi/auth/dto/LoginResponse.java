package com.example.healthyeverythingapi.auth.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoginResponse {

    private boolean success;
    private Data data;
    private Tokens tokens;
    private Error error;

    public static LoginResponse success(String email,
                                        String name,
                                        String accessToken,
                                        String refreshToken,
                                        long accessTokenExpiresIn,
                                        long refreshTokenExpiresIn) {

        LoginResponse response = new LoginResponse();
        response.success = true;

        response.data = new Data(email, name);
        response.tokens = new Tokens(
                accessToken,
                refreshToken,
                accessTokenExpiresIn,
                refreshTokenExpiresIn
        );

        return response;
    }

    public static LoginResponse fail(String code, String message) {
        LoginResponse response = new LoginResponse();
        response.success = false;
        response.error = new Error(code, message);
        return response;
    }

    @Getter
    @Setter
    public static class Data {
        private String email;
        private String name;

        public Data(String email, String name) {
            this.email = email;
            this.name = name;
        }
    }

    @Getter
    @Setter
    public static class Tokens {
        private String accessToken;
        private String refreshToken;
        private long accessTokenExpiresIn;
        private long refreshTokenExpiresIn;

        public Tokens(String accessToken,
                      String refreshToken,
                      long accessTokenExpiresIn,
                      long refreshTokenExpiresIn) {
            this.accessToken = accessToken;
            this.refreshToken = refreshToken;
            this.accessTokenExpiresIn = accessTokenExpiresIn;
            this.refreshTokenExpiresIn = refreshTokenExpiresIn;
        }
    }

    @Getter
    @Setter
    public static class Error {
        private String code;
        private String message;

        public Error(String code, String message) {
            this.code = code;
            this.message = message;
        }
    }
}