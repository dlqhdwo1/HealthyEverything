package com.example.healthyeverythingapi.member.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor // lombok이 제공하는 어노테이션, 클래스에 선언된 모든 필드를 파라미터로 받는 생성자를 컴파일 타임에 자동 생성.
public class TokenResponse {

    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private long expiresIn;

}