package com.example.healthyeverythingapi.member.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class MyPageDetailAlarmResponse {

    private String alarmsubject;
    private String alarmcontent;
}
