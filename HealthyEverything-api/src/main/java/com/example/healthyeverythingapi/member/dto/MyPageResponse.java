package com.example.healthyeverythingapi.member.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class MyPageResponse {

    private String username;
    private String useremail;
    private String userphonenumber;
}
