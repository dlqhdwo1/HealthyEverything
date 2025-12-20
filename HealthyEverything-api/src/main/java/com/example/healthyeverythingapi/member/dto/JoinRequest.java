package com.example.healthyeverythingapi.member.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class JoinRequest {

    @NotBlank
    private String userid;

    @NotBlank
    private String phonenumber;


}
