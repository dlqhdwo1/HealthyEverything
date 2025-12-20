package com.example.healthyeverythingapi;

import com.example.healthyeverythingapi.auth.controller.AuthController;
import com.example.healthyeverythingapi.member.controller.MemberController;
import com.example.healthyeverythingapi.member.dto.JoinRequest;
import com.example.healthyeverythingapi.auth.dto.LoginRequest;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;


class HealthyEverythingApiApplicationTests {

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
    @DisplayName("회원가입 성공: 201 Created + 응답 스펙(UserId/phoneNumber) + tokens 발급")
    void 회원가입_성공_201_토큰발급_응답스펙검증() throws Exception {
        var req = new JoinRequest("star5436", "01011111111");

        mockMvc.perform(post("/api/members/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.UserId").value("star5436"))
                .andExpect(jsonPath("$.data.phoneNumber").value("01011111111"))
                .andExpect(jsonPath("$.tokens.accessToken").exists())
                .andExpect(jsonPath("$.tokens.refreshToken").exists())
                .andExpect(jsonPath("$.tokens.accessTokenExpiresIn").isNumber())
                .andExpect(jsonPath("$.tokens.refreshTokenExpiresIn").isNumber())
                .andExpect(jsonPath("$.error").doesNotExist());
    }

    @Test
    @DisplayName("회원가입 실패(유효성): 400 + INVALID_REQUEST + errors.userid 존재")
    void 회원가입_유효성실패_400() throws Exception {
        var req = new JoinRequest("", "01011111111");

        mockMvc.perform(post("/api/members/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("INVALID_REQUEST"))
                .andExpect(jsonPath("$.errors.userid", notNullValue()));
    }


    @Test
    @DisplayName("회원가입 실패(아이디 중복): 409 + code=DUPLICATE_EMAIL")
    void 회원가입_유저아이디중복_409() throws Exception {
        var req = new JoinRequest("dlwjdrb9412", "01011111111");

        mockMvc.perform(post("/api/members/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("DUPLICATE_EMAIL"));
    }

    @Test
    @DisplayName("마이페이지_기본정보조회_성공_200")
    void 마이페이지_기본정보조회_성공_200() throws Exception {
        mockMvc.perform(get("/api/members/mypage")
                        .header("Authorization", "Bearer test-token")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))

                // ✅ MyPageResponse 필드 검증
                .andExpect(jsonPath("$.username").value("이정구"))
                .andExpect(jsonPath("$.useremail").value("test@test.com"))
                .andExpect(jsonPath("$.userphonenumber").value("01012345678"));
    }

    @Test
    @DisplayName("마이페이지_기본정보조회_실패_토큰없음_null반환")
    void 마이페이지_기본정보조회_실패_토큰없음_null반환() throws Exception {
        mockMvc.perform(get("/api/members/mypage")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("INVALID_CREDENTIALS"))
                .andExpect(jsonPath("$.message").value("인증이 필요합니다."));
    }

    @Test
    @DisplayName("마이페이지_기본정보조회_실패_토큰빈문자_null반환")
    void 마이페이지_기본정보조회_실패_토큰빈문자_null반환() throws Exception {
        mockMvc.perform(get("/api/members/mypage")
                        .header("Authorization", " ")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("INVALID_CREDENTIALS"))
                .andExpect(jsonPath("$.message").value("인증이 필요합니다."));
    }

    @Test
    @DisplayName("마이페이지_리뷰조회_200ok")
    void 마이페이지_리뷰조회_200ok() throws Exception {

        mockMvc.perform(get("/api/members/mypage/review")
                .header("Authorization", "Bearer test-token")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data",hasSize(1)))
                .andExpect(jsonPath("$.data[0].subject").value("정말 친절하고 잘 가르쳐주세요!"));
    }


    @Test
    @DisplayName("마이페이지_리뷰조회_실패_토큰없음_null반환")
    void 마이페이지_리뷰조회_실패_토큰없음_null반환() throws Exception {
        mockMvc.perform(get("/api/members/mypage/review")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("INVALID_CREDENTIALS"))
                .andExpect(jsonPath("$.message").value("인증이 필요합니다."));
    }

    @Test
    @DisplayName("마이페이지_리뷰조회_실패_토큰빈문자_null반환")
    void 마이페이지_리뷰조회_실패_토큰빈문자_null반환() throws Exception {
        mockMvc.perform(get("/api/members/mypage/review")
                        .header("Authorization", " ")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("INVALID_CREDENTIALS"))
                .andExpect(jsonPath("$.message").value("인증이 필요합니다."));
    }


    @Test
    @DisplayName("마이페이지_작성한리뷰상세조회_성공_200")
    void 마이페이지_작성한리뷰상세조회_성공_200() throws Exception {

        String reviewid = "1234";

        mockMvc.perform(get("/api/members/mypage/reviews")
                        .param("reviewid", reviewid)
                        .header("Authorization", "Bearer test-token")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.reviewsubject").value("아주 친절하고 잘 가르쳐 주십니다!"))
                .andExpect(jsonPath("$.reviewcontent").value("작년 7월부터 김땡떙 트레이너 선생님이랑 같이 운동하게 되었는데 정말 친절하고 상세하게 가르쳐 주십니다."));
    }

    @Test
    @DisplayName("마이페이지_작성한리뷰상세조회_실패_토큰없음_null반환")
    void 마이페이지_작성한리뷰상세조회_실패_토큰없음_null반환() throws Exception {

        String reviewid = "1234";

        mockMvc.perform(get("/api/members/mypage/reviews")
                        .param("reviewid", reviewid)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("INVALID_CREDENTIALS"))
                .andExpect(jsonPath("$.message").value("인증이 필요합니다."));


        mockMvc.perform(get("/api/members/mypage/reviews")
                        .param("reviewid", reviewid)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", " "))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("INVALID_CREDENTIALS"))
                .andExpect(jsonPath("$.message").value("인증이 필요합니다."));
    }

    @Test
    @DisplayName("마이페이지_알람조회_성공_200_ok")
    void 마이페이지_알람조회_성공_200_ok() throws Exception {
        mockMvc.perform(get("/api/members/mypage/alarm")
                .header("Authorization", "Bearer test-token")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data",hasSize(1)))
                .andExpect(jsonPath("$.data[0].subject").value("2024신년계획 잊진 않으셨죠?"));

    }

    @Test
    @DisplayName("마이페이지_알람조회_실패_토큰없음_null반환")
    void 마이페이지_알람조회_실패_토큰없음_null반환() throws Exception {
        mockMvc.perform(get("/api/members/mypage/alarm")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("INVALID_CREDENTIALS"))
                .andExpect(jsonPath("$.message").value("인증이 필요합니다."));
    }

    @Test
    @DisplayName("마이페이지_알람조회_실패_토큰빈문자_null반환")
    void 마이페이지_알람조회_실패_토큰빈문자_null반환() throws Exception {
        mockMvc.perform(get("/api/members/mypage/alarm")
                        .header("Authorization", " ")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("INVALID_CREDENTIALS"))
                .andExpect(jsonPath("$.message").value("인증이 필요합니다."));
    }

    @Test
    @DisplayName("마이페이지_알람상세조회_성공_200")
    void 마이페이지_알람상세조회_성공_200() throws Exception {
        String alarmid = "1234";

        mockMvc.perform(get("/api/members/mypage/alarms")
                        .param("alarmid", alarmid)
                .header("Authorization", "Bearer test-token")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.alarmsubject").value("2024년도 얼마 안남았어요"))
                .andExpect(jsonPath("$.alarmcontent").value("무료체험 PT받아보기 한 주의 시작을 건강하게! 이땡땡 회원님의 운동 시작을 헬스의 모든것이 도와드리겠습니다."));
    }


    @Test
    @DisplayName("마이페이지_작성한리뷰상세조회_실패_토큰없음_null반환")
    void 마이페이지_알람상세조회_실패_토큰없음_null반환() throws Exception {

        String alarmid = "1234";

        mockMvc.perform(get("/api/members/mypage/alarms")
                        .param("alarmid", alarmid)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("INVALID_CREDENTIALS"))
                .andExpect(jsonPath("$.message").value("인증이 필요합니다."));


        mockMvc.perform(get("/api/members/mypage/alarms")
                        .param("alarmid", alarmid)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", " "))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("INVALID_CREDENTIALS"))
                .andExpect(jsonPath("$.message").value("인증이 필요합니다."));
    }


    @Test
    @DisplayName("마이페이지_채팅목록조회_성공_200")
    void 마이페이지_채팅목록조회_성공_200() throws Exception {
        mockMvc.perform(get("/api/members/mypage/chat")
                .header("Authorization", "Bearer test-token")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data",hasSize(1)))
                .andExpect(jsonPath("$.data[0].roomid").value(123))
                .andExpect(jsonPath("$.data[0].partnerid").value(1001))
                .andExpect(jsonPath("$.data[0].partnername").value("김떙떙트레이너"))
                .andExpect(jsonPath("$.data[0].partnerprofileimageUrl").value("김떙땡트레이너.jpg"))
                .andExpect(jsonPath("$.data[0].lastmessage").value("다음 주 스케줄 알려드릴게요."))
                .andExpect(jsonPath("$.data[0].lastmessageat").value("20251201"))
                .andExpect(jsonPath("$.data[0].unreadcount").value(3));
    }

    @Test
    @DisplayName("마이페이지_채팅목록조회_실패_토큰없음_null반환")
    void 마이페이지_채팅목록조회_실패_토큰없음_null반환() throws Exception {
        mockMvc.perform(get("/api/members/mypage/chat")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("INVALID_CREDENTIALS"))
                .andExpect(jsonPath("$.message").value("인증이 필요합니다."));
    }

    @Test
    @DisplayName("마이페이지_채팅목록조회_실패_토큰빈문자_null반환")
    void 마이페이지_채팅목록조회_실패_토큰빈문자_null반환() throws Exception {
        mockMvc.perform(get("/api/members/mypage/chat")
                        .header("Authorization", " ")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("INVALID_CREDENTIALS"))
                .andExpect(jsonPath("$.message").value("인증이 필요합니다."));
    }
}