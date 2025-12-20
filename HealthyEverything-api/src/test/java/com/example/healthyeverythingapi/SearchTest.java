package com.example.healthyeverythingapi;

import com.example.healthyeverythingapi.member.controller.MemberController;
import com.example.healthyeverythingapi.search.controller.SearchController;
import com.example.healthyeverythingapi.search.dto.PTSearchRequest;
import com.example.healthyeverythingapi.exception.ApiExceptionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.bytebuddy.utility.dispatcher.JavaDispatcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@WebMvcTest(MemberController.class)
@Import(ApiExceptionHandler.class) // ✅ 예외 핸들러 포함
public class SearchTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();

        mockMvc = MockMvcBuilders
                .standaloneSetup(new SearchController())
                .setControllerAdvice(new ApiExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("PT 찾기 성공: 200 + 응답 스펙 검증")
    void PT찾기_성공() throws Exception {
        var req = new PTSearchRequest(
                "화정역",
                2000,
                "FEMALE",
                "PARKING"
        );

        mockMvc.perform(post("/api/search/trainer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))

                .andExpect(jsonPath("$.code").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("코치 목록 조회에 성공했습니다."))

                .andExpect(jsonPath("$.data.trainerProfile").value("profile.img"))
                .andExpect(jsonPath("$.data.trainerName").value("오떙떙"))
                .andExpect(jsonPath("$.data.certificate", not(emptyString())))
                .andExpect(jsonPath("$.data.price", not(emptyString())))
                .andExpect(jsonPath("$.data.gymLocate", not(emptyString())))

                .andExpect(jsonPath("$.error").value(nullValue()));
    }

    @Test
    @DisplayName("PT 찾기 실패: 404 + NOT EXIST TRAINER")
    void PT찾기_실패_404() throws Exception {
        var req = new PTSearchRequest(
                "없는트레이너",
                1000,
                "MALE",
                "SHOWER"
        );

        mockMvc.perform(post("/api/search/trainer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))

                .andExpect(jsonPath("$.code").value("FAIL"))
                .andExpect(jsonPath("$.data").value(nullValue()))
                .andExpect(jsonPath("$.error.code").value("NOT EXIST TRAINER"))
                .andExpect(jsonPath("$.error.message").value("존재하지 않는 트레이너입니다."));
    }

    @Test
    @DisplayName("트레이너 상세정보 : 200 + 응답 스펙 검증 (DTO 직접 반환)")
    void 트레이너상세정보_성공() throws Exception {

        long trainerId = 1L;

        mockMvc.perform(get("/api/search/trainer/{trainerid}", trainerId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))

                // ✅ TrainerDetailResponse를 그대로 반환한다고 가정: 루트 필드 검증
                .andExpect(jsonPath("$.trainerName").value("이상탁"))
                .andExpect(jsonPath("$.trainerProfile").value("이상탁.img"))
                .andExpect(jsonPath("$.certificate", not(emptyString())))
                .andExpect(jsonPath("$.price", not(emptyString())))
                .andExpect(jsonPath("$.programdescription", not(emptyString())))

                // ✅ review 배열 스펙
                .andExpect(jsonPath("$.review").isArray())
                .andExpect(jsonPath("$.review.length()", greaterThanOrEqualTo(1)))

                .andExpect(jsonPath("$.review[0].reviewId").value("123"))
                .andExpect(jsonPath("$.review[0].userId").value("권동한"))
                .andExpect(jsonPath("$.review[0].review", not(emptyString())));
    }

    @Test
    @DisplayName("트레이너 상세정보 실패 - 존재하지 않는 트레이너 (404)")
    void 트레이너상세정보_존재하지않음_404() throws Exception {

        long trainerId = 999999L;

        mockMvc.perform(get("/api/search/trainer/{trainerId}", trainerId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("TRAINER_NOT_FOUND"));
    }


    @Test
    @DisplayName("최근 본 트레이너 조회 성공 - limit=2 (200)")
    void 최근본트레이너조회_성공_200() throws Exception {

        int limit = 2;

        mockMvc.perform(get("/api/search/trainer/recent-trainers/{limit}", limit)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))

                // ✅ top-level
                .andExpect(jsonPath("$.code").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("최근 본 트레이너 목록 조회에 성공했습니다."))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data", hasSize(2)))

                // ✅ 1번 데이터
                .andExpect(jsonPath("$.data[0].coachId").value(1001))
                .andExpect(jsonPath("$.data[0].trainerName").value("이떙떙"))
                .andExpect(jsonPath("$.data[0].rating").value(5))
                .andExpect(jsonPath("$.data[0].gymlocate").value("코지휘트니스 고양점"))

                // ✅ 2번 데이터
                .andExpect(jsonPath("$.data[1].coachId").value(1002))
                .andExpect(jsonPath("$.data[1].trainerName").value("김땡떙"))
                .andExpect(jsonPath("$.data[1].rating").value(5))
                .andExpect(jsonPath("$.data[1].gymlocate").value("럭키짐"));
    }

    @Test
    @DisplayName("최근 본 트레이너 조회 실패 - limit=0 (400)")
    void 최근본트레이너조회_limit0_실패_400() throws Exception {

        int limit = 0;

        mockMvc.perform(get("/api/search/trainer/recent-trainers/{limit}", limit)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))

                .andExpect(jsonPath("$.code").value("FAIL"))
                .andExpect(jsonPath("$.message").value("INVALID_REQUEST"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    @DisplayName("센터조회_성공")
    void 센터조회_성공_200() throws Exception {
        String keyword = "MK휘트니스";

        mockMvc.perform(get("/api/search/center/{searchKeyword}", keyword)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))

                .andExpect(jsonPath("$.code").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("센터 목록 조회에 성공했습니다."))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data", hasSize(2)))

                .andExpect(jsonPath("$.data[0].centername").value("MK휘트니스"))
                .andExpect(jsonPath("$.data[0].centerprofile").value("MK휘트니스.jpg"))
                .andExpect(jsonPath("$.data[0].centerprice").value("30회 기준 30000원"))
                .andExpect(jsonPath("$.data[0].centerinfo").value("신논현역 도보 7분"))

                .andExpect(jsonPath("$.data[1].centername").value("에이블짐"))
                .andExpect(jsonPath("$.data[1].centerprofile").value("에이블짐.jpg"))
                .andExpect(jsonPath("$.data[1].centerprice").value("30회 기준 20000원"))
                .andExpect(jsonPath("$.data[1].centerinfo").value("신논현역 도보 5분"));
    }

    @Test
    @DisplayName("센터상세정보_성공")
    void 센터상세정보_성공_200() throws Exception {

        String CenterId = "200";

        mockMvc.perform(get("/api/search/center/centerdetail/{centerid}", CenterId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))

                .andExpect(jsonPath("$.gymprofile").value("gym.jpg"))
                .andExpect(jsonPath("$.gymname").value("뉴라이프짐"))
                .andExpect(jsonPath("$.time").value("평일 오전 7:00 ~ 오후 11:00"))
                .andExpect(jsonPath("$.price").value("1개월 100,000원"))
                .andExpect(jsonPath("$.gymlocate").value("뉴라이프짐 고양점"));
    }

    @Test
    @DisplayName("센터상세정보_실패_유효하지않는 센터")
    void 센터상세조회_실패_400() throws Exception {

        String CenterId = "0";

        mockMvc.perform(get("/api/search/center/centerdetail/{centerid}", CenterId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))

                .andExpect(jsonPath("$.code").value("FAIL"))
                .andExpect(jsonPath("$.message").value("INVALID_REQUEST"));
    }


    @Test
    @DisplayName("트레이너자격조회_성공_200")
    void 트레이너자격조회_성공_200() throws Exception {

        String TrainerName = "김땡떙";

        mockMvc.perform(get("/api/search/trainer/certificate/{trainername}", TrainerName)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data", hasSize(1)))

                .andExpect(jsonPath("$.data[0].trainername").value("김동호"))
                .andExpect(jsonPath("$.data[0].trainerprofile").value("김동호.jpg"))
                .andExpect(jsonPath("$.data[0].trainercertificatecount").value(2));
    }

    @Test
    @DisplayName("트레이너자격조회_실패_400")
    void 트레이너자격조회_실패_400() throws Exception {

        mockMvc.perform(get("/api/search/trainer/certificate/{trainername}", " ")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value("FAIL"))
                .andExpect(jsonPath("$.message").value("INVALID_REQUEST"));
    }

    @Test
    @DisplayName("내 PT회원권 목록조회(회원입장)_성공_200")
    void 내PT회원권목록조회회원입장_성공_200() throws Exception {

        mockMvc.perform(get("/api/search/member/memberships")
                        .header("Authorization", "Bearer test-token")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))

                .andExpect(jsonPath("$.memberships").exists())
                .andExpect(jsonPath("$.memberships").isArray())
                .andExpect(jsonPath("$.memberships", hasSize(1)))

                .andExpect(jsonPath("$.memberships[0].membershipId").value(10001))
                .andExpect(jsonPath("$.memberships[0].trainerId").value(7))
                .andExpect(jsonPath("$.memberships[0].trainerName").value("홍길동"))
                .andExpect(jsonPath("$.memberships[0].trainerProfileImageUrl")
                        .value("https://cdn.woondoc.com/trainers/7/profile.jpg"))
                .andExpect(jsonPath("$.memberships[0].gymName").value("강남 헬스짐"))
                .andExpect(jsonPath("$.memberships[0].productName").value("PT 20회권"))
                .andExpect(jsonPath("$.memberships[0].totalSessions").value(20))
                .andExpect(jsonPath("$.memberships[0].remainingSessions").value(20))
                .andExpect(jsonPath("$.memberships[0].status").value("ACTIVE"))
                .andExpect(jsonPath("$.memberships[0].registeredAt")
                        .value("20251212"));
    }

    @Test
    @DisplayName("마이페이지_채팅목록조회_실패_토큰없음_null반환")
    void 내PT회원권목록조회__실패_토큰없음_null반환() throws Exception {
        mockMvc.perform(get("/api/search/member/memberships")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("INVALID_CREDENTIALS"))
                .andExpect(jsonPath("$.message").value("인증이 필요합니다."));
    }

    @Test
    @DisplayName("마이페이지_채팅목록조회_실패_토큰빈문자_null반환")
    void 내PT회원권목록조회_실패_토큰빈문자_null반환() throws Exception {
        mockMvc.perform(get("/api/search/member/memberships")
                        .header("Authorization", " ")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("INVALID_CREDENTIALS"))
                .andExpect(jsonPath("$.message").value("인증이 필요합니다."));
    }
}
