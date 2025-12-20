package com.example.healthyeverythingapi.search.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RecentTrainerResponse {

    private String code;
    private String message;
    private List<RecentTrainer> data;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecentTrainer {
        private Long coachId;
        private String trainerName;
        private int rating;
        private String gymlocate;
    }
}