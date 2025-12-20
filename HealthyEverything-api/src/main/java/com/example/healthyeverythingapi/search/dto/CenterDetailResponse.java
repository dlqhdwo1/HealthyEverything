package com.example.healthyeverythingapi.search.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.GetMapping;

@Getter
@Setter
@AllArgsConstructor
public class CenterDetailResponse {

    private String gymprofile;
    private String gymname;
    private String time;
    private String price;
    private String gymlocate;

}
