package com.example.healthyeverythingapi;

import com.example.healthyeverythingapi.auth.dto.RefreshTokenRequest;
import org.springframework.test.web.servlet.MockMvc;
import com.example.healthyeverythingapi.auth.controller.AuthController;
import com.example.healthyeverythingapi.member.controller.MemberController;
import com.example.healthyeverythingapi.auth.dto.LoginRequest;
import com.example.healthyeverythingapi.exception.ApiExceptionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class AuthTest {


    private MockMvc mockMvc;
    private ObjectMapper objectMapper;


    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();


        var validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders
                .standaloneSetup(
                        new MemberController(),
                        new AuthController()
                )
                .setControllerAdvice(new ApiExceptionHandler())
                .setValidator(validator)
                .build();
    }

    @Test
    @DisplayName("로그인 성공: 200 OK + 토큰 스펙 검증")
    void 로그인_성공_200_토큰스펙검증() throws Exception {
        var req = new LoginRequest("a@test.com", "P@ssw0rd!");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.accessToken", not(emptyString())))
                .andExpect(jsonPath("$.refreshToken", not(emptyString())))
                .andExpect(jsonPath("$.tokenType", anyOf(is("Bearer"), is("bearer"))))
                .andExpect(jsonPath("$.expiresIn", greaterThan(0)));
    }

    @Test
    @DisplayName("로그인 실패(인증 실패): 401 + code=INVALID_CREDENTIALS")
    void 로그인_실패_401() throws Exception {
        var req = new LoginRequest("a@test.com", "wrong!");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("INVALID_CREDENTIALS"));
    }


    @Test
    @DisplayName("로그인 실패(유효성): 400 + code=VALIDATION_ERROR")
    void 로그인_유효성실패_400() throws Exception {
        var req = new LoginRequest("", "");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("INVALID_REQUEST"))
                .andExpect(jsonPath("$.success").value(false));
    }


    @Test
    @DisplayName("토큰 재발급 성공 - 응답 스펙 검증")
    void 토큰_재발급_성공() throws Exception {

        var req = new RefreshTokenRequest("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9","web");

        mockMvc.perform(post("/api/auth/token/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))

                // root
                .andExpect(jsonPath("$.success").value(true))

                // data
                .andExpect(jsonPath("$.data").isMap())
                .andExpect(jsonPath("$.data.email").value("star5436@naver.com"))
                .andExpect(jsonPath("$.data.name").value("이정규"))

                // tokens
                .andExpect(jsonPath("$.tokens").isMap())
                .andExpect(jsonPath("$.tokens.accessToken", not(emptyOrNullString())))
                .andExpect(jsonPath("$.tokens.refreshToken", not(emptyOrNullString())))
                .andExpect(jsonPath("$.tokens.accessTokenExpiresIn").value(3600))
                .andExpect(jsonPath("$.tokens.refreshTokenExpiresIn").value(2592000));
    }

    @Test
    @DisplayName("refreshToken 누락/빈값 - 400 검증")
    void 토큰_재발급_누락_빈값() throws Exception {

        var req = new RefreshTokenRequest("", "web");


        mockMvc.perform(post("/api/auth/token/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("INVALID_REQUEST"))
                .andExpect(jsonPath("$.errors.refreshToken", notNullValue()));
    }

    @Test
    @DisplayName("deviceId 누락/빈값 - 400 검증")
    void 토큰_deviceId_누락_빈값() throws Exception {

        var req = new RefreshTokenRequest("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9", "");

        mockMvc.perform(post("/api/auth/token/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("INVALID_REQUEST"))
                .andExpect(jsonPath("$.errors.deviceId", notNullValue()));
    }

    @Test
    @DisplayName("refreshToken 유효하지 않음 - 401 검증")
    void 토큰_재발급_유효하지않음() throws Exception {

        var req = new RefreshTokenRequest("invalid", "web");

        mockMvc.perform(post("/api/auth/token/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("INVALID_REFRESH_TOKEN"));
    }
}
