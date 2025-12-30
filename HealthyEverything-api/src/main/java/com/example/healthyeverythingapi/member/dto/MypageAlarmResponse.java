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
public class MypageAlarmResponse {

    private List<MypageAlarmResponse.AlarmItem> data;


    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AlarmItem {
        private String subject;
    }
}
