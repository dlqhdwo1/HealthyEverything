package com.example.healthyeverythingapi.member.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class MyPageReviewResponse {
    private List<ReviewItem> data;


    @Getter
    @Setter
    @AllArgsConstructor
    public static class ReviewItem {
        private String subject;
    }

}
