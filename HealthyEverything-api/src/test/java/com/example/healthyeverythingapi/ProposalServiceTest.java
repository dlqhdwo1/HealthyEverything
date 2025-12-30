package com.example.healthyeverythingapi;

import com.example.healthyeverythingapi.proposal.domain.Proposal;
import com.example.healthyeverythingapi.proposal.dto.ProposalRequest;
import com.example.healthyeverythingapi.proposal.dto.ProposalResponse;
import com.example.healthyeverythingapi.proposal.dto.TrainerReceivedProposalDetailResponse;
import com.example.healthyeverythingapi.proposal.dto.TrainerReceivedProposalResponse;
import com.example.healthyeverythingapi.proposal.repository.ProposalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProposalServiceTest {

    @LocalServerPort
    private int port;

    private TestRestTemplate restTemplate;

    @Autowired
    private ProposalRepository proposalRepository;

    private String baseUrl;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port;
        proposalRepository.deleteAll();

        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        restTemplate = new TestRestTemplate(new RestTemplateBuilder().requestFactory(() -> factory));
    }

    @Nested
    @DisplayName("견적 요청 생성 테스트")
    class CreateProposalTest {

        @Test
        @DisplayName("견적 요청 성공 - 201 Created")
        void createProposalSuccess() {
            // given
            ProposalRequest request = new ProposalRequest(
                    '1', '2', '1', "역삼동", "male", '1', '1'
            );

            // when
            ResponseEntity<ProposalResponse> response = restTemplate.postForEntity(
                    baseUrl + "/api/proposal",
                    request,
                    ProposalResponse.class
            );

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getCode()).isEqualTo("SUCCESS");
            assertThat(response.getBody().getData()).isNotNull();
            assertThat(response.getBody().getData().getStatus()).isEqualTo("PENDING");

            // DB 검증
            assertThat(proposalRepository.count()).isEqualTo(1);
        }

        @Test
        @DisplayName("견적 요청 실패 - 나이가 0인 경우 400")
        void createProposalFailAgeZero() {
            // given
            ProposalRequest request = new ProposalRequest(
                    '1', '2', '1', "역삼동", "male", '0', '1'
            );

            // when
            ResponseEntity<Map> response = restTemplate.postForEntity(
                    baseUrl + "/api/proposal",
                    request,
                    Map.class
            );

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

            // DB 검증 - 저장 안됨
            assertThat(proposalRepository.count()).isEqualTo(0);
        }

        @Test
        @DisplayName("견적 요청 실패 - 운동 목적이 0인 경우 400")
        void createProposalFailExercisePurposeZero() {
            // given
            ProposalRequest request = new ProposalRequest(
                    '0', '2', '1', "역삼동", "male", '1', '1'
            );

            // when
            ResponseEntity<Map> response = restTemplate.postForEntity(
                    baseUrl + "/api/proposal",
                    request,
                    Map.class
            );

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

            // DB 검증 - 저장 안됨
            assertThat(proposalRepository.count()).isEqualTo(0);
        }
    }

    @Nested
    @DisplayName("트레이너 견적 목록 조회 테스트")
    class GetTrainerProposalsTest {

        @Test
        @DisplayName("목록 조회 성공 - 200 OK")
        void getTrainerProposalsSuccess() {
            // given - DB에 데이터 저장
            Proposal proposal = Proposal.builder()
                    .exercisePurpose('1')
                    .exerciseExperience('2')
                    .lessonCount('2')
                    .lessonLocate("서울")
                    .gender("male")
                    .age('2')
                    .hopeRequest('1')
                    .status("PENDING")
                    .region("서울")
                    .username("헬린이1")
                    .createdAt("20251227")
                    .expiresAt("20260126")
                    .build();
            proposalRepository.save(proposal);

            // when
            ResponseEntity<TrainerReceivedProposalResponse> response = restTemplate.getForEntity(
                    baseUrl + "/api/proposal/trainer/read?status=PENDING&region=서울",
                    TrainerReceivedProposalResponse.class
            );

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getContent()).hasSize(1);
            assertThat(response.getBody().getContent().get(0).getUsername()).isEqualTo("헬린이1");
        }

        @Test
        @DisplayName("목록 조회 실패 - status가 빈 값 400")
        void getTrainerProposalsFailEmptyStatus() {
            // when
            ResponseEntity<Map> response = restTemplate.getForEntity(
                    baseUrl + "/api/proposal/trainer/read?status=&region=서울",
                    Map.class
            );

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        @Test
        @DisplayName("목록 조회 실패 - region이 빈 값 400")
        void getTrainerProposalsFailEmptyRegion() {
            // when
            ResponseEntity<Map> response = restTemplate.getForEntity(
                    baseUrl + "/api/proposal/trainer/read?status=PENDING&region=",
                    Map.class
            );

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }
    }

    @Nested
    @DisplayName("견적 상세 조회 테스트")
    class GetProposalDetailTest {

        @Test
        @DisplayName("상세 조회 성공 - 200 OK")
        void getProposalDetailSuccess() {
            // given - DB에 데이터 저장
            Proposal proposal = Proposal.builder()
                    .exercisePurpose('1')
                    .exerciseExperience('2')
                    .lessonCount('1')
                    .lessonLocate("도내동")
                    .gender("male")
                    .age('1')
                    .hopeRequest('1')
                    .status("PENDING")
                    .createdAt("20251227")
                    .expiresAt("20260126")
                    .build();
            Proposal saved = proposalRepository.save(proposal);

            // when
            ResponseEntity<TrainerReceivedProposalDetailResponse> response = restTemplate.getForEntity(
                    baseUrl + "/api/proposal/trainer/proposaldetail?requestid=" + saved.getId(),
                    TrainerReceivedProposalDetailResponse.class
            );

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getExercisepurpose()).isEqualTo('1');
            assertThat(response.getBody().getLessonlocate()).isEqualTo("도내동");
            assertThat(response.getBody().getGender()).isEqualTo("male");
        }

        @Test
        @DisplayName("상세 조회 실패 - requestId가 빈 값 400")
        void getProposalDetailFailEmptyRequestId() {
            // when
            ResponseEntity<Map> response = restTemplate.getForEntity(
                    baseUrl + "/api/proposal/trainer/proposaldetail?requestid=",
                    Map.class
            );

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        @Test
        @DisplayName("상세 조회 실패 - 존재하지 않는 견적 400")
        void getProposalDetailFailNotFound() {
            // when
            ResponseEntity<Map> response = restTemplate.getForEntity(
                    baseUrl + "/api/proposal/trainer/proposaldetail?requestid=99999",
                    Map.class
            );

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }
    }

    @Test
    @DisplayName("견적 요청 후 DB 데이터 직접 확인")
    void createProposalAndVerifyInDatabase() {
        // given
        ProposalRequest request1 = new ProposalRequest('1', '2', '1', "역삼동", "male", '1', '1');
        ProposalRequest request2 = new ProposalRequest('2', '1', '2', "강남동", "female", '2', '2');

        // when
        restTemplate.postForEntity(baseUrl + "/api/proposal", request1, ProposalResponse.class);
        restTemplate.postForEntity(baseUrl + "/api/proposal", request2, ProposalResponse.class);

        // then - DB에서 직접 조회
        System.out.println("\n============================================");
        System.out.println("         [H2 DB 저장 데이터 확인]");
        System.out.println("============================================");

        proposalRepository.findAll().forEach(proposal -> {
            System.out.println("ID: " + proposal.getId());
            System.out.println("운동목적: " + proposal.getExercisePurpose());
            System.out.println("지역: " + proposal.getLessonLocate());
            System.out.println("상태: " + proposal.getStatus());
            System.out.println("--------------------------------------------");
        });

        System.out.println("총 " + proposalRepository.count() + "개의 견적이 DB에 저장됨");
        System.out.println("============================================\n");

        assertThat(proposalRepository.count()).isEqualTo(2);
    }
}
