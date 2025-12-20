package com.example.healthyeverythingapi.proposal.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ProposalRequest {
    private char exercisePurpose;  //1.기초체력을 늘리고싶어요 , 2. 체중을 감랴이할거에요
    private char exerciseExperience; // 1. 운동을 거의안해봤어요. 2. 다른 종목의 운동을 해봤어요
    private char lessonCount; // 1.10회 , 2.20회 , 3.30회
    private String lessonLocate; // 역삼동
    private String gender; // male
    private char age; // 1.10대 2. 20대 3.30대 4.40대
    private char hopeRequest; // 1.이름아침(06~09) , 2.아침(09~12)

}
