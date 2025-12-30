package com.example.healthyeverythingapi.search.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CenterDetailResponse {

    private String gymprofile;
    private String gymname;
    private String time;
    private String price;
    private String gymlocate;

}
