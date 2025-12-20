package com.example.healthyeverythingapi.search.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PTSearchRequest {

    private String searchKeyword; //트레이너,인근지하철,센터,지역,검색필드
    private int ssarchDistance; // 검색 반경 필드
    private String trainerGender; // 트레이너 성별
    private String provide; // 이용편의 제공여부 필드
}
