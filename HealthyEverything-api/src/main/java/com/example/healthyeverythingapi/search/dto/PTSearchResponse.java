package com.example.healthyeverythingapi.search.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PTSearchResponse {

    private String code;
    private String message;
    private Data data;
    private Error error;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Data {
        private String trainerProfile;
        private String trainerName;
        private String certificate;
        private String price;
        private String gymLocate;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Error {
        private String code;
        private String message;
    }
}