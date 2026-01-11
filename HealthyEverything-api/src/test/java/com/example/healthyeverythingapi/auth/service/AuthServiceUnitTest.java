package com.example.healthyeverythingapi.auth.service;

import com.example.healthyeverythingapi.auth.dto.AuthResponses;
import com.example.healthyeverythingapi.auth.dto.LoginRequest;
import com.example.healthyeverythingapi.auth.jwt.JwtTokenProvider;
import com.example.healthyeverythingapi.common.exception.DuplicateEmailException;
import com.example.healthyeverythingapi.common.exception.InvalidCredentialsException;
import com.example.healthyeverythingapi.member.dto.JoinRequest;
import com.example.healthyeverythingapi.user.domain.User;
import com.example.healthyeverythingapi.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AuthServiceUnitTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private AuthService authService;

    @Nested
    @DisplayName("회원가입 유닛 테스트")
    class SignupUnitTest {

        @Test
        @DisplayName("회원가입 성공 - 정상적인 요청")
        void signupSuccess() {
            // given
            JoinRequest request = new JoinRequest("test@example.com", "password123", "홍길동");

            given(userRepository.existsByEmail("test@example.com")).willReturn(false);
            given(passwordEncoder.encode("password123")).willReturn("encodedPassword");
            given(userRepository.save(any(User.class))).willAnswer(invocation -> {
                User user = invocation.getArgument(0);
                return User.builder()
                        .id(1L)
                        .email(user.getEmail())
                        .passwordHash(user.getPasswordHash())
                        .name(user.getName())
                        .build();
            });

            // when
            AuthResponses.SignupResponse response = authService.signup(request);

            // then
            assertThat(response.getUserId()).isEqualTo(1L);
            assertThat(response.getEmail()).isEqualTo("test@example.com");
            assertThat(response.getName()).isEqualTo("홍길동");

            verify(userRepository).existsByEmail("test@example.com");
            verify(passwordEncoder).encode("password123");
            verify(userRepository).save(any(User.class));
        }

        @Test
        @DisplayName("회원가입 실패 - 이메일 중복")
        void signupFailDuplicateEmail() {
            // given
            JoinRequest request = new JoinRequest("duplicate@example.com", "password123", "홍길동");

            given(userRepository.existsByEmail("duplicate@example.com")).willReturn(true);

            // when & then
            assertThatThrownBy(() -> authService.signup(request))
                    .isInstanceOf(DuplicateEmailException.class);

            verify(userRepository).existsByEmail("duplicate@example.com");
            verify(userRepository, never()).save(any(User.class));
        }
    }

    @Nested
    @DisplayName("로그인 유닛 테스트")
    class LoginUnitTest {

        @Test
        @DisplayName("로그인 성공 - 올바른 이메일과 비밀번호")
        void loginSuccess() {
            // given
            LoginRequest request = new LoginRequest("test@example.com", "password123");

            User user = User.builder()
                    .id(1L)
                    .email("test@example.com")
                    .passwordHash("encodedPassword")
                    .name("홍길동")
                    .build();

            given(userRepository.findByEmail("test@example.com")).willReturn(Optional.of(user));
            given(passwordEncoder.matches("password123", "encodedPassword")).willReturn(true);
            given(jwtTokenProvider.createAccessToken(1L, "test@example.com")).willReturn("test-access-token");
            given(jwtTokenProvider.createRefreshToken(1L, "test@example.com")).willReturn("test-refresh-token");
            given(jwtTokenProvider.getAccessTokenValidityInSeconds()).willReturn(3600L);
            given(jwtTokenProvider.getRefreshTokenValidityInSeconds()).willReturn(604800L);

            // when
            AuthResponses.LoginResponse response = authService.login(request);

            // then
            assertThat(response.getAccessToken()).isEqualTo("test-access-token");
            assertThat(response.getRefreshToken()).isEqualTo("test-refresh-token");
            assertThat(response.getAccessTokenExpiresIn()).isEqualTo(3600L);
            assertThat(response.getRefreshTokenExpiresIn()).isEqualTo(604800L);
            assertThat(response.getUser().getId()).isEqualTo(1L);
            assertThat(response.getUser().getEmail()).isEqualTo("test@example.com");
            assertThat(response.getUser().getName()).isEqualTo("홍길동");

            verify(userRepository).findByEmail("test@example.com");
            verify(passwordEncoder).matches("password123", "encodedPassword");
            verify(jwtTokenProvider).createAccessToken(1L, "test@example.com");
            verify(jwtTokenProvider).createRefreshToken(1L, "test@example.com");
        }

        @Test
        @DisplayName("로그인 실패 - 존재하지 않는 이메일")
        void loginFailUserNotFound() {
            // given
            LoginRequest request = new LoginRequest("notfound@example.com", "password123");

            given(userRepository.findByEmail("notfound@example.com")).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> authService.login(request))
                    .isInstanceOf(InvalidCredentialsException.class);

            verify(userRepository).findByEmail("notfound@example.com");
            verify(passwordEncoder, never()).matches(anyString(), anyString());
        }

        @Test
        @DisplayName("로그인 실패 - 비밀번호 불일치")
        void loginFailWrongPassword() {
            // given
            LoginRequest request = new LoginRequest("test@example.com", "wrongPassword");

            User user = User.builder()
                    .id(1L)
                    .email("test@example.com")
                    .passwordHash("encodedPassword")
                    .name("홍길동")
                    .build();

            given(userRepository.findByEmail("test@example.com")).willReturn(Optional.of(user));
            given(passwordEncoder.matches("wrongPassword", "encodedPassword")).willReturn(false);

            // when & then
            assertThatThrownBy(() -> authService.login(request))
                    .isInstanceOf(InvalidCredentialsException.class);

            verify(userRepository).findByEmail("test@example.com");
            verify(passwordEncoder).matches("wrongPassword", "encodedPassword");
        }
    }

    @Nested
    @DisplayName("토큰 갱신 유닛 테스트")
    class RefreshTokenUnitTest {

        @Test
        @DisplayName("토큰 갱신 성공 - 유효한 리프레시 토큰")
        void refreshTokenSuccess() {
            // given
            String refreshToken = "valid-refresh-token";

            User user = User.builder()
                    .id(1L)
                    .email("test@example.com")
                    .passwordHash("encodedPassword")
                    .name("홍길동")
                    .build();

            given(jwtTokenProvider.validateToken(refreshToken)).willReturn(true);
            given(jwtTokenProvider.getEmailFromToken(refreshToken)).willReturn("test@example.com");
            given(jwtTokenProvider.getUserIdFromToken(refreshToken)).willReturn(1L);
            given(userRepository.findByEmail("test@example.com")).willReturn(Optional.of(user));
            given(jwtTokenProvider.createAccessToken(1L, "test@example.com")).willReturn("new-access-token");
            given(jwtTokenProvider.createRefreshToken(1L, "test@example.com")).willReturn("new-refresh-token");
            given(jwtTokenProvider.getAccessTokenValidityInSeconds()).willReturn(3600L);
            given(jwtTokenProvider.getRefreshTokenValidityInSeconds()).willReturn(604800L);

            // when
            AuthResponses.LoginResponse response = authService.refreshToken(refreshToken);

            // then
            assertThat(response.getAccessToken()).isEqualTo("new-access-token");
            assertThat(response.getRefreshToken()).isEqualTo("new-refresh-token");

            verify(jwtTokenProvider).validateToken(refreshToken);
            verify(jwtTokenProvider).getEmailFromToken(refreshToken);
            verify(userRepository).findByEmail("test@example.com");
        }

        @Test
        @DisplayName("토큰 갱신 실패 - 유효하지 않은 리프레시 토큰")
        void refreshTokenFailInvalidToken() {
            // given
            String invalidToken = "invalid-refresh-token";

            given(jwtTokenProvider.validateToken(invalidToken)).willReturn(false);

            // when & then
            assertThatThrownBy(() -> authService.refreshToken(invalidToken))
                    .isInstanceOf(InvalidCredentialsException.class);

            verify(jwtTokenProvider).validateToken(invalidToken);
            verify(userRepository, never()).findByEmail(anyString());
        }
    }
}
