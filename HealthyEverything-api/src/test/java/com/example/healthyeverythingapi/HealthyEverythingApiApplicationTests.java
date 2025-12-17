package com.example.healthyeverythingapi;

import com.example.healthyeverythingapi.controller.AuthController;
import com.example.healthyeverythingapi.controller.MemberController;
import com.example.healthyeverythingapi.dto.LoginRequest;
import com.example.healthyeverythingapi.dto.SignupRequest;
import com.example.healthyeverythingapi.exception.ApiExceptionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
class HealthyEverythingApiApplicationTests {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();

        // ✅ standaloneSetup 에서 @Valid 동작하려면 Validator를 직접 붙여주는 게 안전함
        var validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders
                .standaloneSetup(
                        new MemberController(),
                        new AuthController()
                )
                // ✅ 네가 만든 예외 핸들러로 교체해줘
                .setControllerAdvice(new ApiExceptionHandler())
             //   .setMessageConverters(new InvalidCredentialsException(objectMapper))
                .setValidator(validator)
                .build();
    }

    @Test
    @DisplayName("회원가입 성공: 201 Created + 응답 스펙(id/email/name) + password 없음")
    void 회원가입_성공_201_응답스펙검증() throws Exception {
        var req = new SignupRequest("a@test.com", "P@ssw0rd!", "홍길동");

        mockMvc.perform(post("/api/v1/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.email").value("a@test.com"))
                .andExpect(jsonPath("$.name").value("홍길동"))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    @DisplayName("회원가입 실패(유효성): 400 + code=VALIDATION_ERROR + errors.email 존재")
    void 회원가입_유효성실패_400() throws Exception {
        var req = new SignupRequest("", "P@ssw0rd!", "홍길동");

        mockMvc.perform(post("/api/v1/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.errors.email", notNullValue()));
    }

    @Test
    @DisplayName("회원가입 실패(이메일 중복): 409 + code=DUPLICATE_EMAIL")
    void 회원가입_이메일중복_409() throws Exception {
        var req = new SignupRequest("dup@test.com", "P@ssw0rd!", "홍길동");

        mockMvc.perform(post("/api/v1/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("DUPLICATE_EMAIL"));
    }

    @Test
    @DisplayName("로그인 성공: 200 OK + 토큰 스펙 검증")
    void 로그인_성공_200_토큰스펙검증() throws Exception {
        var req = new LoginRequest("a@test.com", "P@ssw0rd!");

        mockMvc.perform(post("/api/v1/auth/login")
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

        mockMvc.perform(post("/api/v1/auth/login")
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

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }
}