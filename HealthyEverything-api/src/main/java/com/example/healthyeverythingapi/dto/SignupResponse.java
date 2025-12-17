package com.example.healthyeverythingapi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SignupResponse {

    private Long id;
    private String email;
    private String name;

}