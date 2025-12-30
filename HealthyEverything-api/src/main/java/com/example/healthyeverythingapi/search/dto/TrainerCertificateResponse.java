package com.example.healthyeverythingapi.search.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TrainerCertificateResponse {

    private List<TrainerData> data;

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TrainerData {

        private String trainername;
        private String trainerprofile;
        private String trainercertificatecount;
    }

}
