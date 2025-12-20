package com.example.healthyeverythingapi.search.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class TrainerCertificateResponse {

    private List<TrainerData> data;

    @Getter
    @AllArgsConstructor
    public static class TrainerData {

        private String trainername;
        private String trainerprofile;
        private String trainercertificatecount;
    }

}
