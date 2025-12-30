package com.example.healthyeverythingapi.search.service;

import com.example.healthyeverythingapi.common.exception.InvalidCredentialsException;
import com.example.healthyeverythingapi.search.domain.Center;
import com.example.healthyeverythingapi.search.domain.Trainer;
import com.example.healthyeverythingapi.search.dto.*;
import com.example.healthyeverythingapi.search.repository.CenterRepository;
import com.example.healthyeverythingapi.search.repository.TrainerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final TrainerRepository trainerRepository;
    private final CenterRepository centerRepository;

    @Transactional(readOnly = true)
    public PTSearchResponse searchTrainer(PTSearchRequest request) {
        return trainerRepository.findBySearchKeyword(request.getSearchKeyword())
                .map(trainer -> new PTSearchResponse(
                        "SUCCESS",
                        "코치 목록 조회에 성공했습니다.",
                        new PTSearchResponse.Data(
                                trainer.getProfileImage(),
                                trainer.getName(),
                                trainer.getCertificate(),
                                trainer.getPrice(),
                                trainer.getGymLocate()
                        ),
                        null
                ))
                .orElse(new PTSearchResponse(
                        "FAIL",
                        null,
                        null,
                        new PTSearchResponse.Error("NOT EXIST TRAINER", "존재하지 않는 트레이너입니다.")
                ));
    }

    @Transactional(readOnly = true)
    public TrainerDetailResponse getTrainerDetail(Long trainerId) {
        Trainer trainer = trainerRepository.findById(trainerId)
                .orElseThrow(() -> new IllegalArgumentException("TRAINER_NOT_FOUND"));

        return new TrainerDetailResponse(
                trainer.getName(),
                trainer.getProfileImage(),
                trainer.getCertificate(),
                trainer.getPrice(),
                trainer.getProgramDescription(),
                List.of(new TrainerDetailResponse.Review("1", "리뷰어", "좋은 트레이너입니다."))
        );
    }

    @Transactional(readOnly = true)
    public RecentTrainerResponse getRecentTrainers(Integer limit) {
        if (limit <= 0) {
            throw new IllegalArgumentException("INVALID_REQUEST");
        }

        List<Trainer> trainers = trainerRepository.findAll();
        List<RecentTrainerResponse.RecentTrainer> recentTrainers = trainers.stream()
                .limit(limit)
                .map(t -> new RecentTrainerResponse.RecentTrainer(
                        t.getId(),
                        t.getName(),
                        t.getRating() != null ? t.getRating() : 5,
                        t.getGymLocate()
                ))
                .toList();

        return new RecentTrainerResponse(
                "SUCCESS",
                "최근 본 트레이너 목록 조회에 성공했습니다.",
                recentTrainers
        );
    }

    @Transactional(readOnly = true)
    public CenterInfoResponse searchCenter(String keyword) {
        List<Center> centers = centerRepository.findByNameContaining(keyword);

        List<CenterInfoResponse.Center> centerList = centers.stream()
                .map(c -> new CenterInfoResponse.Center(
                        c.getName(),
                        c.getProfileImage(),
                        c.getPrice(),
                        c.getInfo()
                ))
                .toList();

        return new CenterInfoResponse(
                "SUCCESS",
                "센터 목록 조회에 성공했습니다.",
                centerList
        );
    }

    @Transactional(readOnly = true)
    public CenterDetailResponse getCenterDetail(String centerId) {
        if (centerId.isBlank() || "0".equals(centerId)) {
            throw new IllegalArgumentException("INVALID_REQUEST");
        }

        Center center = centerRepository.findById(Long.parseLong(centerId))
                .orElseThrow(() -> new IllegalArgumentException("CENTER_NOT_FOUND"));

        return new CenterDetailResponse(
                center.getProfileImage(),
                center.getName(),
                center.getTime(),
                center.getPrice(),
                center.getLocate()
        );
    }

    @Transactional(readOnly = true)
    public TrainerCertificateResponse getTrainerCertificate(String trainerName) {
        if (trainerName == null || trainerName.isBlank()) {
            throw new IllegalArgumentException("INVALID_REQUEST");
        }

        List<Trainer> trainers = trainerRepository.findByNameContaining(trainerName);

        List<TrainerCertificateResponse.TrainerData> data = trainers.stream()
                .map(t -> new TrainerCertificateResponse.TrainerData(
                        t.getName(),
                        t.getProfileImage(),
                        t.getCertificate() != null ? String.valueOf(t.getCertificate().split(",").length) : "0"
                ))
                .toList();

        return new TrainerCertificateResponse(data);
    }

    public MemberMembershipsResponse getMemberMemberships(String authorization) {
        if (authorization == null || authorization.isBlank()) {
            throw new InvalidCredentialsException("인증이 필요합니다.");
        }

        return new MemberMembershipsResponse(
                List.of(new MemberMembershipsResponse.Membership(
                        10001L, 7L, "홍길동",
                        "https://cdn.woondoc.com/trainers/7/profile.jpg",
                        "강남 헬스짐", "PT 20회권",
                        20, 20, "ACTIVE", "20251212"
                ))
        );
    }
}
