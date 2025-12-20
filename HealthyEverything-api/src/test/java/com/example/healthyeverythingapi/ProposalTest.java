package com.example.healthyeverythingapi;


import com.example.healthyeverythingapi.proposal.controller.ProposalController;
import com.example.healthyeverythingapi.proposal.dto.ProposalRequest;
import com.example.healthyeverythingapi.proposal.dto.TrainerReceivedProposalResponse;
import com.example.healthyeverythingapi.search.controller.SearchController;
import com.example.healthyeverythingapi.search.dto.PTSearchRequest;
import com.example.healthyeverythingapi.exception.ApiExceptionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.bytebuddy.utility.dispatcher.JavaDispatcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

public class ProposalTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;


    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();

        mockMvc = MockMvcBuilders
                .standaloneSetup(new ProposalController())
                .setControllerAdvice(new ApiExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("최저가 견적받기 성공")
    void 최저가_견적받기_성공_200() throws Exception {
        var req = new ProposalRequest(
                '1',
                '2',
                '1',
                "역삼동",
                "male",
                '1',
                '1'
        );
        mockMvc.perform(post("/api/proposal")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))

                .andExpect(jsonPath("$.code").value("SUCCESS"))
                .andExpect(jsonPath("$.data.requestid").value(12L))
                .andExpect(jsonPath("$.data.status").value("PENDING"))
                .andExpect(jsonPath("$.data.createdat").value("20251209"))
                .andExpect(jsonPath("$.data.expiresat").value("20251231"));

    }

    @Test
    @DisplayName("최저가 견적받기 실패_400")
    void 최저가_견적받기_실패_400() throws Exception {

        var req = new ProposalRequest(
                '0',
                '0',
                '0',
                "",
                "",
                '0',
                '0'
        );

        mockMvc.perform(post("/api/proposal")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))

                .andExpect(jsonPath("$.code").value("FAIL"))
                .andExpect(jsonPath("$.message").value("INVALID_REQUEST"));
    }

    @Test
    @DisplayName("트레이너 견적요청 목록조회")
    void 트레이너견적요청목록조회_성공_200() throws Exception {

        String status = "PENDEING";
        String region = "서울";

        mockMvc.perform(get("/api/proposal/trainer/read")
                        .param("status",status)
                        .param("region", region)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))

                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].requestid").value(12345))
                .andExpect(jsonPath("$.content[0].username").value("헬린이1"))
                .andExpect(jsonPath("$.content[0].lessonlocate").value("서울"))
                .andExpect(jsonPath("$.content[0].sessioncount").value(20));

    }

    @Test
    @DisplayName("트레이너 견적요청 목록조회_실패_400 (status/region 누락 또는 빈값)")
    void 트레이너견적요청목록조회_실패_400() throws Exception {

        // 1) status 빈값 → 400 기대
        mockMvc.perform(get("/api/proposal/trainer/read")
                        .param("status", "")          // ✅ 빈값
                        .param("region", "역삼동")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value("FAIL"))
                .andExpect(jsonPath("$.message").value("INVALID_REQUEST"));

        // 2) region 누락 → 400 기대
        mockMvc.perform(get("/api/proposal/trainer/read")
                        .param("status", "PENDING")
                        .param("region", "")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value("FAIL"))
                .andExpect(jsonPath("$.message").value("INVALID_REQUEST"));

        // 3) status 누락 → 400 기대
        mockMvc.perform(get("/api/proposal/trainer/read")
                        .param("status", "")
                        .param("region", "역삼동")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value("FAIL"))
                .andExpect(jsonPath("$.message").value("INVALID_REQUEST"));
    }

    @Test
    @DisplayName("트레이너 견적요청 상세조회")
    void 트레이너견적요청상세조회_성공_200() throws Exception {

        mockMvc.perform(get("/api/proposal/trainer/proposaldetail")
                .param("requestid",  "10L")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))

                .andExpect(jsonPath("$.exercisepurpose").value("1"))
                .andExpect(jsonPath("$.exerciseexperience").value("2"))
                .andExpect(jsonPath("$.lessoncount").value("1"))
                .andExpect(jsonPath("$.lessonlocate").value("도내동"))
                .andExpect(jsonPath("$.gender").value("male"))
                .andExpect(jsonPath("$.age").value("1"))
                .andExpect(jsonPath("$.lessontime").value("3"))
                .andExpect(jsonPath("$.hoperequest").value("1"));
    }

    @Test
    @DisplayName("트레이너 견적요청 상세조회_실패_400")
    void 트레이너견적요청상세조회_실패_400() throws Exception {
        // 1) status 빈값 → 400 기대
        mockMvc.perform(get("/api/proposal/trainer/proposaldetail")
                        .param("requestid", "")          // ✅ 빈값
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value("FAIL"))
                .andExpect(jsonPath("$.message").value("INVALID_REQUEST"));

    }
}
