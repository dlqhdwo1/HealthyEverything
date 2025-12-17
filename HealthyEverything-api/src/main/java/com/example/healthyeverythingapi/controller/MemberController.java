package com.example.healthyeverythingapi.controller;

import com.example.healthyeverythingapi.dto.SignupRequest;
import com.example.healthyeverythingapi.dto.SignupResponse;
import com.example.healthyeverythingapi.exception.DuplicateEmailException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/members")
public class MemberController {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SignupResponse signup(@Valid @RequestBody SignupRequest request) {

        // 임시 로직 (나중에 Service로 분리)
        if ("dup@test.com".equals(request.getEmail())) {
            throw new DuplicateEmailException();
        }

        return new SignupResponse(
                1L,
                request.getEmail(),
                request.getName()
        );
    }
}