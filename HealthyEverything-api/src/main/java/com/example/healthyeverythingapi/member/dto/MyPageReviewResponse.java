package com.example.healthyeverythingapi.member.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MyPageReviewResponse {
    private List<ReviewItem> data;


    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ReviewItem {
        private String subject;
    }

}
