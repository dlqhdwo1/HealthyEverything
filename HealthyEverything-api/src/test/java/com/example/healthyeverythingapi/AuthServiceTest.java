package com.example.healthyeverythingapi;

import com.example.healthyeverythingapi.auth.dto.AuthResponses;
import com.example.healthyeverythingapi.auth.dto.LoginRequest;
import com.example.healthyeverythingapi.auth.dto.RefreshTokenRequest;
import com.example.healthyeverythingapi.member.dto.JoinRequest;
import com.example.healthyeverythingapi.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthServiceTest {

    @LocalServerPort
    private int port;

    private TestRestTemplate restTemplate;
    private String baseUrl;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port;
        userRepository.deleteAll();

        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        restTemplate = new TestRestTemplate(new RestTemplateBuilder().requestFactory(() -> factory));
    }

    @Nested
    @DisplayName("회원가입 테스트")
    class SignupTest {

        @Test
        @DisplayName("회원가입 성공 - 정상적인 요청")
        void signupSuccess() {
            // given
            JoinRequest request = new JoinRequest("test@example.com", "password123", "홍길동");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<JoinRequest> entity = new HttpEntity<>(request, headers);

            // when
            ResponseEntity<AuthResponses.SignupResponse> response = restTemplate.exchange(
                    baseUrl + "/api/members/join",
                    HttpMethod.POST,
                    entity,
                    AuthResponses.SignupResponse.class
            );

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getUserId()).isNotNull();
            assertThat(response.getBody().getEmail()).isEqualTo("test@example.com");
            assertThat(response.getBody().getName()).isEqualTo("홍길동");

            // DB 검증
            assertThat(userRepository.existsByEmail("test@example.com")).isTrue();
        }

        @Test
        @DisplayName("회원가입 실패 - 이메일 중복")
        void signupFailDuplicateEmail() {
            // given - 먼저 사용자 생성
            JoinRequest firstRequest = new JoinRequest("duplicate@example.com", "password123", "첫번째");
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<JoinRequest> firstEntity = new HttpEntity<>(firstRequest, headers);
            restTemplate.exchange(baseUrl + "/api/members/join", HttpMethod.POST, firstEntity, AuthResponses.SignupResponse.class);

            // 중복 이메일로 가입 시도
            JoinRequest duplicateRequest = new JoinRequest("duplicate@example.com", "password456", "두번째");
            HttpEntity<JoinRequest> duplicateEntity = new HttpEntity<>(duplicateRequest, headers);

            // when
            ResponseEntity<Map> response = restTemplate.exchange(
                    baseUrl + "/api/members/join",
                    HttpMethod.POST,
                    duplicateEntity,
                    Map.class
            );

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().get("code")).isEqualTo("DUPLICATE_EMAIL");
        }
    }

    @Nested
    @DisplayName("로그인 테스트")
    class LoginTest {

        @Test
        @DisplayName("로그인 성공 - 올바른 이메일과 비밀번호")
        void loginSuccess() {
            // given - 먼저 회원가입
            JoinRequest joinRequest = new JoinRequest("login@example.com", "password123", "홍길동");
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<JoinRequest> joinEntity = new HttpEntity<>(joinRequest, headers);
            restTemplate.exchange(baseUrl + "/api/members/join", HttpMethod.POST, joinEntity, AuthResponses.SignupResponse.class);

            // 로그인 시도
            LoginRequest loginRequest = new LoginRequest("login@example.com", "password123");
            HttpEntity<LoginRequest> loginEntity = new HttpEntity<>(loginRequest, headers);

            // when
            ResponseEntity<AuthResponses.LoginResponse> response = restTemplate.exchange(
                    baseUrl + "/api/auth/login",
                    HttpMethod.POST,
                    loginEntity,
                    AuthResponses.LoginResponse.class
            );

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getAccessToken()).isNotBlank();
        }

        @Test
        @DisplayName("로그인 실패 - 존재하지 않는 이메일")
        void loginFailUserNotFound() {
            // given
            LoginRequest loginRequest = new LoginRequest("notfound@example.com", "password123");
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<LoginRequest> loginEntity = new HttpEntity<>(loginRequest, headers);

            // when
            ResponseEntity<Map> response = restTemplate.exchange(
                    baseUrl + "/api/auth/login",
                    HttpMethod.POST,
                    loginEntity,
                    Map.class
            );

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().get("code")).isEqualTo("INVALID_CREDENTIALS");
        }

        @Test
        @DisplayName("로그인 실패 - 비밀번호 불일치")
        void loginFailWrongPassword() {
            // given - 먼저 회원가입
            JoinRequest joinRequest = new JoinRequest("wrongpw@example.com", "correctPassword", "홍길동");
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<JoinRequest> joinEntity = new HttpEntity<>(joinRequest, headers);
            restTemplate.exchange(baseUrl + "/api/members/join", HttpMethod.POST, joinEntity, AuthResponses.SignupResponse.class);

            // 잘못된 비밀번호로 로그인 시도
            LoginRequest loginRequest = new LoginRequest("wrongpw@example.com", "wrongPassword");
            HttpEntity<LoginRequest> loginEntity = new HttpEntity<>(loginRequest, headers);

            // when
            ResponseEntity<Map> response = restTemplate.exchange(
                    baseUrl + "/api/auth/login",
                    HttpMethod.POST,
                    loginEntity,
                    Map.class
            );

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().get("code")).isEqualTo("INVALID_CREDENTIALS");
        }
    }

    @Nested
    @DisplayName("토큰 갱신 테스트")
    class TokenRefreshTest {

        @Test
        @DisplayName("토큰 갱신 성공")
        void refreshTokenSuccess() {
            // given
            RefreshTokenRequest request = new RefreshTokenRequest("valid-refresh-token", "device-123");
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<RefreshTokenRequest> entity = new HttpEntity<>(request, headers);

            // when
            ResponseEntity<Map> response = restTemplate.exchange(
                    baseUrl + "/api/auth/token/refresh",
                    HttpMethod.POST,
                    entity,
                    Map.class
            );

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().get("success")).isEqualTo(true);
            assertThat(response.getBody().get("tokens")).isNotNull();
        }

        @Test
        @DisplayName("토큰 갱신 실패 - 유효하지 않은 토큰")
        void refreshTokenFailInvalidToken() {
            // given
            RefreshTokenRequest request = new RefreshTokenRequest("invalid", "device-123");
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<RefreshTokenRequest> entity = new HttpEntity<>(request, headers);

            // when
            ResponseEntity<Map> response = restTemplate.exchange(
                    baseUrl + "/api/auth/token/refresh",
                    HttpMethod.POST,
                    entity,
                    Map.class
            );

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().get("success")).isEqualTo(false);
            assertThat(response.getBody().get("message")).isEqualTo("INVALID_REFRESH_TOKEN");
        }
    }
}
