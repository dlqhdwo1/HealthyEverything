package com.example.healthyeverythingapi.proposal.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TrainerReceivedProposalDetailResponse {

    private char exercisepurpose;
    private char exerciseexperience;
    private char lessoncount;
    private String lessonlocate;
    private String gender;
    private char age;
    private char lessontime;
    private char hoperequest;
}
