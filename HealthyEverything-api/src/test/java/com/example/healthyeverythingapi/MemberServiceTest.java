package com.example.healthyeverythingapi;

import com.example.healthyeverythingapi.member.domain.Alarm;
import com.example.healthyeverythingapi.member.domain.ChatRoom;
import com.example.healthyeverythingapi.member.domain.MemberProfile;
import com.example.healthyeverythingapi.member.domain.Review;
import com.example.healthyeverythingapi.member.dto.*;
import com.example.healthyeverythingapi.member.repository.AlarmRepository;
import com.example.healthyeverythingapi.member.repository.ChatRoomRepository;
import com.example.healthyeverythingapi.member.repository.MemberProfileRepository;
import com.example.healthyeverythingapi.member.repository.ReviewRepository;
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
class MemberServiceTest {

    @LocalServerPort
    private int port;

    private TestRestTemplate restTemplate;
    private String baseUrl;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MemberProfileRepository memberProfileRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private AlarmRepository alarmRepository;


    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port;

        // 모든 데이터 삭제
        chatRoomRepository.deleteAll();
        alarmRepository.deleteAll();
        reviewRepository.deleteAll();
        memberProfileRepository.deleteAll();
        userRepository.deleteAll();

        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        restTemplate = new TestRestTemplate(new RestTemplateBuilder().requestFactory(() -> factory));
    }

    @Nested
    @DisplayName("마이페이지 기본정보 테스트")
    class MyPageBasicTest {

        @Test
        @DisplayName("마이페이지_기본정보조회_성공_200")
        void 마이페이지_기본정보조회_성공_200() {
            // given
            MemberProfile profile = MemberProfile.builder()
                    .userId(1L)
                    .username("이정구")
                    .useremail("test@test.com")
                    .userphonenumber("01012345678")
                    .build();
            memberProfileRepository.save(profile);

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer test-token");
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            // when
            ResponseEntity<MyPageResponse> response = restTemplate.exchange(
                    baseUrl + "/api/members/mypage",
                    HttpMethod.GET,
                    entity,
                    MyPageResponse.class
            );

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getUsername()).isEqualTo("이정구");
            assertThat(response.getBody().getUseremail()).isEqualTo("test@test.com");
            assertThat(response.getBody().getUserphonenumber()).isEqualTo("01012345678");
        }

        @Test
        @DisplayName("마이페이지_기본정보조회_실패_토큰없음")
        void 마이페이지_기본정보조회_실패_토큰없음() {
            // when
            ResponseEntity<Map> response = restTemplate.getForEntity(
                    baseUrl + "/api/members/mypage",
                    Map.class
            );

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().get("code")).isEqualTo("INVALID_CREDENTIALS");
            assertThat(response.getBody().get("message")).isEqualTo("인증이 필요합니다.");
        }

        @Test
        @DisplayName("마이페이지_기본정보조회_실패_토큰빈문자")
        void 마이페이지_기본정보조회_실패_토큰빈문자() {
            // given
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", " ");
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            // when
            ResponseEntity<Map> response = restTemplate.exchange(
                    baseUrl + "/api/members/mypage",
                    HttpMethod.GET,
                    entity,
                    Map.class
            );

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().get("code")).isEqualTo("INVALID_CREDENTIALS");
            assertThat(response.getBody().get("message")).isEqualTo("인증이 필요합니다.");
        }
    }

    @Nested
    @DisplayName("마이페이지 리뷰 테스트")
    class MyPageReviewTest {

        @Test
        @DisplayName("마이페이지_리뷰조회_성공_200")
        void 마이페이지_리뷰조회_성공_200() {
            // given
            Review review = Review.builder()
                    .userId(1L)
                    .subject("정말 친절하고 잘 가르쳐주세요!")
                    .content("상세 내용")
                    .build();
            reviewRepository.save(review);

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer test-token");
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            // when
            ResponseEntity<MyPageReviewResponse> response = restTemplate.exchange(
                    baseUrl + "/api/members/mypage/review",
                    HttpMethod.GET,
                    entity,
                    MyPageReviewResponse.class
            );

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getData()).hasSize(1);
            assertThat(response.getBody().getData().get(0).getSubject()).isEqualTo("정말 친절하고 잘 가르쳐주세요!");
        }

        @Test
        @DisplayName("마이페이지_리뷰조회_실패_토큰없음")
        void 마이페이지_리뷰조회_실패_토큰없음() {
            // when
            ResponseEntity<Map> response = restTemplate.getForEntity(
                    baseUrl + "/api/members/mypage/review",
                    Map.class
            );

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().get("code")).isEqualTo("INVALID_CREDENTIALS");
        }

        @Test
        @DisplayName("마이페이지_리뷰조회_실패_토큰빈문자")
        void 마이페이지_리뷰조회_실패_토큰빈문자() {
            // given
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", " ");
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            // when
            ResponseEntity<Map> response = restTemplate.exchange(
                    baseUrl + "/api/members/mypage/review",
                    HttpMethod.GET,
                    entity,
                    Map.class
            );

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().get("code")).isEqualTo("INVALID_CREDENTIALS");
        }

        @Test
        @DisplayName("마이페이지_작성한리뷰상세조회_성공_200")
        void 마이페이지_작성한리뷰상세조회_성공_200() {
            // given
            Review review = Review.builder()
                    .userId(1L)
                    .subject("아주 친절하고 잘 가르쳐 주십니다!")
                    .content("작년 7월부터 김땡떙 트레이너 선생님이랑 같이 운동하게 되었는데 정말 친절하고 상세하게 가르쳐 주십니다.")
                    .build();
            Review savedReview = reviewRepository.save(review);

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer test-token");
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            // when
            ResponseEntity<MyPageDetailReViewResponse> response = restTemplate.exchange(
                    baseUrl + "/api/members/mypage/reviews?reviewid=" + savedReview.getId(),
                    HttpMethod.GET,
                    entity,
                    MyPageDetailReViewResponse.class
            );

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getReviewsubject()).isEqualTo("아주 친절하고 잘 가르쳐 주십니다!");
            assertThat(response.getBody().getReviewcontent()).isEqualTo("작년 7월부터 김땡떙 트레이너 선생님이랑 같이 운동하게 되었는데 정말 친절하고 상세하게 가르쳐 주십니다.");
        }

        @Test
        @DisplayName("마이페이지_작성한리뷰상세조회_실패_토큰없음")
        void 마이페이지_작성한리뷰상세조회_실패_토큰없음() {
            // when
            ResponseEntity<Map> response = restTemplate.getForEntity(
                    baseUrl + "/api/members/mypage/reviews?reviewid=1234",
                    Map.class
            );

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().get("code")).isEqualTo("INVALID_CREDENTIALS");
        }
    }

    @Nested
    @DisplayName("마이페이지 알람 테스트")
    class MyPageAlarmTest {

        @Test
        @DisplayName("마이페이지_알람조회_성공_200")
        void 마이페이지_알람조회_성공_200() {
            // given
            Alarm alarm = Alarm.builder()
                    .userId(1L)
                    .subject("2024신년계획 잊진 않으셨죠?")
                    .content("알람 내용")
                    .build();
            alarmRepository.save(alarm);

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer test-token");
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            // when
            ResponseEntity<MypageAlarmResponse> response = restTemplate.exchange(
                    baseUrl + "/api/members/mypage/alarm",
                    HttpMethod.GET,
                    entity,
                    MypageAlarmResponse.class
            );

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getData()).hasSize(1);
            assertThat(response.getBody().getData().get(0).getSubject()).isEqualTo("2024신년계획 잊진 않으셨죠?");
        }

        @Test
        @DisplayName("마이페이지_알람조회_실패_토큰없음")
        void 마이페이지_알람조회_실패_토큰없음() {
            // when
            ResponseEntity<Map> response = restTemplate.getForEntity(
                    baseUrl + "/api/members/mypage/alarm",
                    Map.class
            );

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().get("code")).isEqualTo("INVALID_CREDENTIALS");
        }

        @Test
        @DisplayName("마이페이지_알람조회_실패_토큰빈문자")
        void 마이페이지_알람조회_실패_토큰빈문자() {
            // given
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", " ");
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            // when
            ResponseEntity<Map> response = restTemplate.exchange(
                    baseUrl + "/api/members/mypage/alarm",
                    HttpMethod.GET,
                    entity,
                    Map.class
            );

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().get("code")).isEqualTo("INVALID_CREDENTIALS");
        }

        @Test
        @DisplayName("마이페이지_알람상세조회_성공_200")
        void 마이페이지_알람상세조회_성공_200() {
            // given
            Alarm alarm = Alarm.builder()
                    .userId(1L)
                    .subject("2024년도 얼마 안남았어요")
                    .content("무료체험 PT받아보기 한 주의 시작을 건강하게! 이땡땡 회원님의 운동 시작을 헬스의 모든것이 도와드리겠습니다.")
                    .build();
            Alarm savedAlarm = alarmRepository.save(alarm);

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer test-token");
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            // when
            ResponseEntity<MyPageDetailAlarmResponse> response = restTemplate.exchange(
                    baseUrl + "/api/members/mypage/alarms?alarmid=" + savedAlarm.getId(),
                    HttpMethod.GET,
                    entity,
                    MyPageDetailAlarmResponse.class
            );

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getAlarmsubject()).isEqualTo("2024년도 얼마 안남았어요");
            assertThat(response.getBody().getAlarmcontent()).isEqualTo("무료체험 PT받아보기 한 주의 시작을 건강하게! 이땡땡 회원님의 운동 시작을 헬스의 모든것이 도와드리겠습니다.");
        }

        @Test
        @DisplayName("마이페이지_알람상세조회_실패_토큰없음")
        void 마이페이지_알람상세조회_실패_토큰없음() {
            // when
            ResponseEntity<Map> response = restTemplate.getForEntity(
                    baseUrl + "/api/members/mypage/alarms?alarmid=1234",
                    Map.class
            );

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().get("code")).isEqualTo("INVALID_CREDENTIALS");
        }
    }

    @Nested
    @DisplayName("마이페이지 채팅 테스트")
    class MyPageChatTest {

        @Test
        @DisplayName("마이페이지_채팅목록조회_성공_200")
        void 마이페이지_채팅목록조회_성공_200() {
            // given
            ChatRoom chatRoom = ChatRoom.builder()
                    .userId(1L)
                    .partnerId(1001L)
                    .partnerName("김떙떙트레이너")
                    .partnerProfileImageUrl("김떙땡트레이너.jpg")
                    .lastMessage("다음 주 스케줄 알려드릴게요.")
                    .lastMessageAt("20251201")
                    .unreadCount(3)
                    .build();
            ChatRoom savedChatRoom = chatRoomRepository.save(chatRoom);

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer test-token");
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            // when
            ResponseEntity<MyPageChatResponse> response = restTemplate.exchange(
                    baseUrl + "/api/members/mypage/chat",
                    HttpMethod.GET,
                    entity,
                    MyPageChatResponse.class
            );

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getData()).hasSize(1);
            assertThat(response.getBody().getData().get(0).getRoomid()).isEqualTo(savedChatRoom.getId());
            assertThat(response.getBody().getData().get(0).getPartnerid()).isEqualTo(1001);
            assertThat(response.getBody().getData().get(0).getPartnername()).isEqualTo("김떙떙트레이너");
            assertThat(response.getBody().getData().get(0).getPartnerprofileimageUrl()).isEqualTo("김떙땡트레이너.jpg");
            assertThat(response.getBody().getData().get(0).getLastmessage()).isEqualTo("다음 주 스케줄 알려드릴게요.");
            assertThat(response.getBody().getData().get(0).getLastmessageat()).isEqualTo("20251201");
            assertThat(response.getBody().getData().get(0).getUnreadcount()).isEqualTo(3);
        }

        @Test
        @DisplayName("마이페이지_채팅목록조회_실패_토큰없음")
        void 마이페이지_채팅목록조회_실패_토큰없음() {
            // when
            ResponseEntity<Map> response = restTemplate.getForEntity(
                    baseUrl + "/api/members/mypage/chat",
                    Map.class
            );

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().get("code")).isEqualTo("INVALID_CREDENTIALS");
        }

        @Test
        @DisplayName("마이페이지_채팅목록조회_실패_토큰빈문자")
        void 마이페이지_채팅목록조회_실패_토큰빈문자() {
            // given
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", " ");
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            // when
            ResponseEntity<Map> response = restTemplate.exchange(
                    baseUrl + "/api/members/mypage/chat",
                    HttpMethod.GET,
                    entity,
                    Map.class
            );

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().get("code")).isEqualTo("INVALID_CREDENTIALS");
        }
    }
}
