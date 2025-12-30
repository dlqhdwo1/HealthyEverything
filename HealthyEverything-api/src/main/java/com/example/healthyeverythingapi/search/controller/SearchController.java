package com.example.healthyeverythingapi.search.controller;

import com.example.healthyeverythingapi.search.dto.*;
import com.example.healthyeverythingapi.search.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    @PostMapping("/trainer")
    public ResponseEntity<PTSearchResponse> search(@RequestBody PTSearchRequest request) {
        PTSearchResponse response = searchService.searchTrainer(request);
        if ("FAIL".equals(response.getCode())) {
            return ResponseEntity.status(404).body(response);
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/trainer/{trainerid}")
    public TrainerDetailResponse trainerDetail(@PathVariable Long trainerid) {
        return searchService.getTrainerDetail(trainerid);
    }

    public static class TrainerNotFoundException extends RuntimeException {
        public TrainerNotFoundException() {
            super("TRAINER_NOT_FOUND");
        }
    }

    @GetMapping("/trainer/recent-trainers/{limit}")
    public RecentTrainerResponse recentTrainers(@PathVariable Integer limit) {
        return searchService.getRecentTrainers(limit);
    }

    @GetMapping("/center/{searchKeyword}")
    public CenterInfoResponse CenterSearch(@PathVariable("searchKeyword") String searchKeyword) {
        return searchService.searchCenter(searchKeyword);
    }

    @GetMapping("/center/centerdetail/{centerid}")
    public CenterDetailResponse CenterDetail(@PathVariable("centerid") String centerid) {
        return searchService.getCenterDetail(centerid);
    }

    @GetMapping("/trainer/certificate/{trainername}")
    public TrainerCertificateResponse trainerCertificate(@PathVariable("trainername") String trainername) {
        return searchService.getTrainerCertificate(trainername);
    }

    @GetMapping("/member/memberships")
    public MemberMembershipsResponse memberMemberships(@RequestHeader(value = "Authorization", required = false) String authorization) {
        return searchService.getMemberMemberships(authorization);
    }
}
