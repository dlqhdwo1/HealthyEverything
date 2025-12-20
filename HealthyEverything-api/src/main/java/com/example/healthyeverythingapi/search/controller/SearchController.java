package com.example.healthyeverythingapi.search.controller;

import com.example.healthyeverythingapi.exception.InvalidCredentialsException;
import com.example.healthyeverythingapi.search.dto.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/search")
public class SearchController {

    @PostMapping("/trainer")
    public ResponseEntity<PTSearchResponse> search(@RequestBody PTSearchRequest request) {

        if ("없는트레이너".equals(request.getSearchKeyword())) {
            return ResponseEntity.status(404)
                    .body(new PTSearchResponse(
                            "FAIL",
                            null,
                            null,
                            new PTSearchResponse.Error(
                                    "NOT EXIST TRAINER",
                                    "존재하지 않는 트레이너입니다."
                            )
                    ));
        }

        return ResponseEntity.ok(
                new PTSearchResponse(
                        "SUCCESS",
                        "코치 목록 조회에 성공했습니다.",
                        new PTSearchResponse.Data(
                                "profile.img",
                                "오떙떙",
                                "체형,기능 교정과 재활 기반의 근력 향상 트레이닝 전문",
                                "30회 기준 회당 50,000원",
                                "스포애니 화정역점"
                        ),
                        null
                )
        );
    }

    @GetMapping("/trainer/{trainerid}")
    public TrainerDetailResponse trainerDetail(@PathVariable Long trainerid) {

        if (trainerid == 999999L) {
            throw new TrainerNotFoundException();
        }

        return new TrainerDetailResponse(
                "이상탁",
                "이상탁.img",
                "생활스포츠지도사2급,nasmcpt",
                "1회당 50000원",
                "다이어트 근육증가",
                List.of(
                        new TrainerDetailResponse.Review(
                                "123",
                                "권동한",
                                "체중을 감량한후 키워 몸을 만들고싶어서 선생님 찾음."
                        )
                )
        );
    }

    public class TrainerNotFoundException extends RuntimeException {
        public TrainerNotFoundException() {
            super("TRAINER_NOT_FOUND");
        }
    }

    @GetMapping("/trainer/recent-trainers/{limit}")
    public RecentTrainerResponse recentTrainers(@PathVariable Integer limit) {
        if (limit <= 0) {
            throw new IllegalArgumentException("INVALID_REQUEST");
        }

        List<RecentTrainerResponse.RecentTrainer> trainers = List.of(
                new RecentTrainerResponse.RecentTrainer(
                        1001L,
                        "이떙떙",
                        5,
                        "코지휘트니스 고양점"
                ),
                new RecentTrainerResponse.RecentTrainer(
                        1002L,
                        "김땡떙",
                        5,
                        "럭키짐"
                )
        ).subList(0, Math.min(limit, 2));

        return new RecentTrainerResponse(
                "SUCCESS",
                "최근 본 트레이너 목록 조회에 성공했습니다.",
                trainers
        );
    }


    @GetMapping("/center/{searchKeyword}")
    public CenterInfoResponse CenterSearch(@PathVariable("searchKeyword") String searchKeyword) {
        return new CenterInfoResponse(
                "SUCCESS",
                "센터 목록 조회에 성공했습니다.",
                List.of(
                        new CenterInfoResponse.Center(
                                "MK휘트니스",
                                "MK휘트니스.jpg",
                                "30회 기준 30000원",
                                "신논현역 도보 7분"
                        ),
                        new CenterInfoResponse.Center(
                                "에이블짐",
                                "에이블짐.jpg",
                                "30회 기준 20000원",
                                "신논현역 도보 5분"
                        )
                )
        );
    }


    @GetMapping("/center/centerdetail/{centerid}")
    public CenterDetailResponse CenterDetail(@PathVariable("centerid") String centerid) {

        if (centerid.isBlank() || "0".equals(centerid)) {
            throw new IllegalArgumentException();
        }

        return new CenterDetailResponse(
                "gym.jpg",
                "뉴라이프짐",
                "평일 오전 7:00 ~ 오후 11:00",
                "1개월 100,000원",
                "뉴라이프짐 고양점"
        );
    }

    @GetMapping("/trainer/certificate/{trainername}")
    public TrainerCertificateResponse trainerCertificate(@PathVariable("trainername") String trainername) {

        if (trainername == null || trainername.isBlank()) {
            throw new IllegalArgumentException("INVALID_REQUEST");
        }


        return new TrainerCertificateResponse(
                List.of(
                        new TrainerCertificateResponse.TrainerData(
                                "김동호",
                                "김동호.jpg",
                                "2"
                        )
                )
        );
    }

    @GetMapping("/member/memberships")
    public MemberMembershipsResponse memberMemberships(@RequestHeader(value = "Authorization", required = false) String authorization) {
        if (authorization == null || authorization.isBlank()) {
            throw new InvalidCredentialsException("인증이 필요합니다.");
        }

        return new MemberMembershipsResponse(
                List.of(
                        new MemberMembershipsResponse.Membership(
                                10001L,
                                7L,
                                "홍길동",
                                "https://cdn.woondoc.com/trainers/7/profile.jpg",
                                "강남 헬스짐",
                                "PT 20회권",
                                20,
                                20,
                                "ACTIVE",
                                "20251212"
                        )
                )
        );
    }
}