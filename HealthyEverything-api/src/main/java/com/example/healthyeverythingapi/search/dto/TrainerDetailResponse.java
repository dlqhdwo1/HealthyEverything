package com.example.healthyeverythingapi.search.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TrainerDetailResponse {

    private String trainerName;
    private String trainerProfile;
    private String certificate;
    private String price;
    private String programdescription;
    private List<Review> review;

    // ✅ 내부 클래스 (ReviewResponse를 따로 만들지 않음)
    @Getter
    @Setter
    @NoArgsConstructor
    public static class Review {
        private String reviewId;
        private String userId;
        private String review;

        public Review(String reviewId, String userId, String review) {
            this.reviewId = reviewId;
            this.userId = userId;
            this.review = review;
        }
    }
}
