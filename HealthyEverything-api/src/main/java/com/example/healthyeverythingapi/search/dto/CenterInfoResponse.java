package com.example.healthyeverythingapi.search.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class CenterInfoResponse {

    private String code;
    private String message;
    private List<Center> data;

    // ✅ 센터 정보 내부 DTO
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Center {

        private String centername;
        private String centerprofile;
        private String centerprice;
        private String centerinfo;

    }
}