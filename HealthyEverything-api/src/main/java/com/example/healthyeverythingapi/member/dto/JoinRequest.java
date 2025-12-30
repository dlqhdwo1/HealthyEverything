package com.example.healthyeverythingapi.member.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class JoinRequest {

    @Email @NotBlank
    private String userid;

    @NotBlank @Size(min = 8, max = 50)
    private String password;

    @NotBlank @Size(max = 30)
    private String name;
}
