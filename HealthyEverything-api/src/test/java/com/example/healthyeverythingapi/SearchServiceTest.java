package com.example.healthyeverythingapi;

import com.example.healthyeverythingapi.search.domain.Center;
import com.example.healthyeverythingapi.search.domain.Trainer;
import com.example.healthyeverythingapi.search.dto.*;
import com.example.healthyeverythingapi.search.repository.CenterRepository;
import com.example.healthyeverythingapi.search.repository.TrainerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
class SearchServiceTest {

    @LocalServerPort
    private int port;

    private TestRestTemplate restTemplate;
    private String baseUrl;

    @Autowired
    private TrainerRepository trainerRepository;

    @Autowired
    private CenterRepository centerRepository;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port;
        trainerRepository.deleteAll();
        centerRepository.deleteAll();

        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        restTemplate = new TestRestTemplate(new RestTemplateBuilder().requestFactory(() -> factory));
    }

    @Test
    @DisplayName("PT 찾기 성공: 200 + 응답 스펙 검증")
    void PT찾기_성공() {
        // given
        Trainer trainer = Trainer.builder()
                .name("오떙떙")
                .profileImage("profile.img")
                .certificate("생활체육지도사")
                .price("50000원")
                .gymLocate("화정역")
                .searchKeyword("화정역")
                .rating(5)
                .build();
        trainerRepository.save(trainer);

        PTSearchRequest request = new PTSearchRequest("화정역", 2000, "FEMALE", "PARKING");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<PTSearchRequest> entity = new HttpEntity<>(request, headers);

        // when
        ResponseEntity<PTSearchResponse> response = restTemplate.exchange(
                baseUrl + "/api/search/trainer",
                HttpMethod.POST,
                entity,
                PTSearchResponse.class
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCode()).isEqualTo("SUCCESS");
        assertThat(response.getBody().getMessage()).isEqualTo("코치 목록 조회에 성공했습니다.");
        assertThat(response.getBody().getData()).isNotNull();
        assertThat(response.getBody().getData().getTrainerName()).isEqualTo("오떙떙");
        assertThat(response.getBody().getData().getTrainerProfile()).isEqualTo("profile.img");
        assertThat(response.getBody().getError()).isNull();
    }

    @Test
    @DisplayName("PT 찾기 실패: 404 + NOT EXIST TRAINER")
    void PT찾기_실패_404() {
        // given
        PTSearchRequest request = new PTSearchRequest("없는트레이너", 1000, "MALE", "SHOWER");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<PTSearchRequest> entity = new HttpEntity<>(request, headers);

        // when
        ResponseEntity<PTSearchResponse> response = restTemplate.exchange(
                baseUrl + "/api/search/trainer",
                HttpMethod.POST,
                entity,
                PTSearchResponse.class
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCode()).isEqualTo("FAIL");
        assertThat(response.getBody().getData()).isNull();
        assertThat(response.getBody().getError()).isNotNull();
        assertThat(response.getBody().getError().getCode()).isEqualTo("NOT EXIST TRAINER");
        assertThat(response.getBody().getError().getMessage()).isEqualTo("존재하지 않는 트레이너입니다.");
    }

    @Test
    @DisplayName("트레이너 상세정보 : 200 + 응답 스펙 검증")
    void 트레이너상세정보_성공() {
        // given
        Trainer trainer = Trainer.builder()
                .name("이상탁")
                .profileImage("이상탁.img")
                .certificate("생활체육지도사")
                .price("60000원")
                .programDescription("1:1 맞춤 PT")
                .gymLocate("강남역")
                .rating(5)
                .build();
        Trainer savedTrainer = trainerRepository.save(trainer);

        // when
        ResponseEntity<TrainerDetailResponse> response = restTemplate.getForEntity(
                baseUrl + "/api/search/trainer/" + savedTrainer.getId(),
                TrainerDetailResponse.class
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTrainerName()).isEqualTo("이상탁");
        assertThat(response.getBody().getTrainerProfile()).isEqualTo("이상탁.img");
        assertThat(response.getBody().getCertificate()).isNotBlank();
        assertThat(response.getBody().getPrice()).isNotBlank();
        assertThat(response.getBody().getProgramdescription()).isNotBlank();
        assertThat(response.getBody().getReview()).isNotNull();
    }

    @Test
    @DisplayName("트레이너 상세정보 실패 - 존재하지 않는 트레이너 (400)")
    void 트레이너상세정보_존재하지않음_400() {
        // when
        ResponseEntity<Map> response = restTemplate.getForEntity(
                baseUrl + "/api/search/trainer/999999",
                Map.class
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("code")).isEqualTo("FAIL");
        assertThat(response.getBody().get("message")).isEqualTo("TRAINER_NOT_FOUND");
    }

    @Test
    @DisplayName("최근 본 트레이너 조회 성공 - limit=2 (200)")
    void 최근본트레이너조회_성공_200() {
        // given
        Trainer trainer1 = Trainer.builder()
                .name("이떙떙")
                .profileImage("이떙떙.img")
                .certificate("생활체육지도사")
                .price("50000원")
                .gymLocate("코지휘트니스 고양점")
                .rating(5)
                .build();
        Trainer trainer2 = Trainer.builder()
                .name("김땡떙")
                .profileImage("김땡떙.img")
                .certificate("생활체육지도사")
                .price("55000원")
                .gymLocate("럭키짐")
                .rating(5)
                .build();
        trainerRepository.save(trainer1);
        trainerRepository.save(trainer2);

        // when
        ResponseEntity<RecentTrainerResponse> response = restTemplate.getForEntity(
                baseUrl + "/api/search/trainer/recent-trainers/2",
                RecentTrainerResponse.class
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCode()).isEqualTo("SUCCESS");
        assertThat(response.getBody().getMessage()).isEqualTo("최근 본 트레이너 목록 조회에 성공했습니다.");
        assertThat(response.getBody().getData()).hasSize(2);
        assertThat(response.getBody().getData().get(0).getTrainerName()).isEqualTo("이떙떙");
        assertThat(response.getBody().getData().get(0).getRating()).isEqualTo(5);
        assertThat(response.getBody().getData().get(0).getGymlocate()).isEqualTo("코지휘트니스 고양점");
        assertThat(response.getBody().getData().get(1).getTrainerName()).isEqualTo("김땡떙");
    }

    @Test
    @DisplayName("최근 본 트레이너 조회 실패 - limit=0 (400)")
    void 최근본트레이너조회_limit0_실패_400() {
        // when
        ResponseEntity<Map> response = restTemplate.getForEntity(
                baseUrl + "/api/search/trainer/recent-trainers/0",
                Map.class
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("code")).isEqualTo("FAIL");
        assertThat(response.getBody().get("message")).isEqualTo("INVALID_REQUEST");
    }

    @Test
    @DisplayName("센터조회_성공")
    void 센터조회_성공_200() {
        // given
        Center center1 = Center.builder()
                .name("MK휘트니스")
                .profileImage("MK휘트니스.jpg")
                .price("30회 기준 30000원")
                .info("신논현역 도보 7분")
                .time("06:00 ~ 23:00")
                .locate("서울시 강남구")
                .build();
        Center center2 = Center.builder()
                .name("MK휘트니스 강남점")
                .profileImage("에이블짐.jpg")
                .price("30회 기준 20000원")
                .info("신논현역 도보 5분")
                .time("06:00 ~ 22:00")
                .locate("서울시 강남구")
                .build();
        centerRepository.save(center1);
        centerRepository.save(center2);

        // when
        ResponseEntity<CenterInfoResponse> response = restTemplate.getForEntity(
                baseUrl + "/api/search/center/MK휘트니스",
                CenterInfoResponse.class
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCode()).isEqualTo("SUCCESS");
        assertThat(response.getBody().getMessage()).isEqualTo("센터 목록 조회에 성공했습니다.");
        assertThat(response.getBody().getData()).hasSize(2);
    }

    @Test
    @DisplayName("센터상세정보_성공")
    void 센터상세정보_성공_200() {
        // given
        Center center = Center.builder()
                .name("뉴라이프짐")
                .profileImage("gym.jpg")
                .price("1개월 100,000원")
                .info("뉴라이프짐 정보")
                .time("평일 오전 7:00 ~ 오후 11:00")
                .locate("뉴라이프짐 고양점")
                .build();
        Center savedCenter = centerRepository.save(center);

        // when
        ResponseEntity<CenterDetailResponse> response = restTemplate.getForEntity(
                baseUrl + "/api/search/center/centerdetail/" + savedCenter.getId(),
                CenterDetailResponse.class
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getGymprofile()).isEqualTo("gym.jpg");
        assertThat(response.getBody().getGymname()).isEqualTo("뉴라이프짐");
        assertThat(response.getBody().getTime()).isEqualTo("평일 오전 7:00 ~ 오후 11:00");
        assertThat(response.getBody().getPrice()).isEqualTo("1개월 100,000원");
        assertThat(response.getBody().getGymlocate()).isEqualTo("뉴라이프짐 고양점");
    }

    @Test
    @DisplayName("센터상세정보_실패_유효하지않는 센터")
    void 센터상세조회_실패_400() {
        // when
        ResponseEntity<Map> response = restTemplate.getForEntity(
                baseUrl + "/api/search/center/centerdetail/0",
                Map.class
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("code")).isEqualTo("FAIL");
        assertThat(response.getBody().get("message")).isEqualTo("INVALID_REQUEST");
    }

    @Test
    @DisplayName("트레이너자격조회_성공_200")
    void 트레이너자격조회_성공_200() {
        // given
        Trainer trainer = Trainer.builder()
                .name("김동호")
                .profileImage("김동호.jpg")
                .certificate("생활체육지도사,피트니스 자격증")
                .price("50000원")
                .gymLocate("강남역")
                .rating(5)
                .build();
        trainerRepository.save(trainer);

        // when
        ResponseEntity<TrainerCertificateResponse> response = restTemplate.getForEntity(
                baseUrl + "/api/search/trainer/certificate/김동호",
                TrainerCertificateResponse.class
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getData()).hasSize(1);
        assertThat(response.getBody().getData().get(0).getTrainername()).isEqualTo("김동호");
        assertThat(response.getBody().getData().get(0).getTrainerprofile()).isEqualTo("김동호.jpg");
        assertThat(response.getBody().getData().get(0).getTrainercertificatecount()).isEqualTo("2");
    }

    @Test
    @DisplayName("트레이너자격조회_실패_400")
    void 트레이너자격조회_실패_400() {
        // given
        java.net.URI uri = java.net.URI.create(baseUrl + "/api/search/trainer/certificate/%20");

        // when
        ResponseEntity<Map> response = restTemplate.getForEntity(uri, Map.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("code")).isEqualTo("FAIL");
        assertThat(response.getBody().get("message")).isEqualTo("INVALID_REQUEST");
    }

    @Test
    @DisplayName("내 PT회원권 목록조회(회원입장)_성공_200")
    void 내PT회원권목록조회회원입장_성공_200() {
        // given
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer test-token");
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        // when
        ResponseEntity<MemberMembershipsResponse> response = restTemplate.exchange(
                baseUrl + "/api/search/member/memberships",
                HttpMethod.GET,
                entity,
                MemberMembershipsResponse.class
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMemberships()).hasSize(1);
        assertThat(response.getBody().getMemberships().get(0).getMembershipId()).isEqualTo(10001);
        assertThat(response.getBody().getMemberships().get(0).getTrainerId()).isEqualTo(7);
        assertThat(response.getBody().getMemberships().get(0).getTrainerName()).isEqualTo("홍길동");
        assertThat(response.getBody().getMemberships().get(0).getGymName()).isEqualTo("강남 헬스짐");
        assertThat(response.getBody().getMemberships().get(0).getProductName()).isEqualTo("PT 20회권");
        assertThat(response.getBody().getMemberships().get(0).getTotalSessions()).isEqualTo(20);
        assertThat(response.getBody().getMemberships().get(0).getRemainingSessions()).isEqualTo(20);
        assertThat(response.getBody().getMemberships().get(0).getStatus()).isEqualTo("ACTIVE");
    }

    @Test
    @DisplayName("마이페이지_채팅목록조회_실패_토큰없음_null반환")
    void 내PT회원권목록조회__실패_토큰없음_null반환() {
        // when
        ResponseEntity<Map> response = restTemplate.getForEntity(
                baseUrl + "/api/search/member/memberships",
                Map.class
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("code")).isEqualTo("INVALID_CREDENTIALS");
        assertThat(response.getBody().get("message")).isEqualTo("인증이 필요합니다.");
    }

    @Test
    @DisplayName("마이페이지_채팅목록조회_실패_토큰빈문자_null반환")
    void 내PT회원권목록조회_실패_토큰빈문자_null반환() {
        // given
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", " ");
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        // when
        ResponseEntity<Map> response = restTemplate.exchange(
                baseUrl + "/api/search/member/memberships",
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
