package com.example.healthyeverythingapi.proposal.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TrainerReceivedProposalResponse {

    private List<Content> content;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Content {
        private Long requestid;
        private String username;
        private String lessonlocate;
        private int sessioncount;
    }
}