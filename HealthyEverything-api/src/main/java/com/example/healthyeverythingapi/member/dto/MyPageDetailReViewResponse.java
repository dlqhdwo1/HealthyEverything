package com.example.healthyeverythingapi.member.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MyPageDetailReViewResponse {
    private String reviewsubject;
    private String reviewcontent;
}
