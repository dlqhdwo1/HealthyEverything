package com.example.healthyeverythingapi.proposal.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ProposalResponse {

    private String code;
    private Data data;


    @Getter
    @Setter
    @AllArgsConstructor
    public static class Data {
        private Long requestid;
        private String status;
        private String createdat;
        private String expiresat;
    }

}
