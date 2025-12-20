package com.example.healthyeverythingapi.member.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class MypageAlarmResponse {

    private List<MypageAlarmResponse.AlarmItem> data;


    @Getter
    @Setter
    @AllArgsConstructor
    public static class AlarmItem {
        private String subject;
    }
}
