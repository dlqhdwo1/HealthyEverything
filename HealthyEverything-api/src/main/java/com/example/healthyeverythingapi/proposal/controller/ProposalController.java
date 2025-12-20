package com.example.healthyeverythingapi.proposal.controller;

import com.example.healthyeverythingapi.proposal.dto.ProposalRequest;
import com.example.healthyeverythingapi.proposal.dto.ProposalResponse;
import com.example.healthyeverythingapi.proposal.dto.TrainerReceivedProposalDetailResponse;
import com.example.healthyeverythingapi.proposal.dto.TrainerReceivedProposalResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/proposal")
public class ProposalController {

    @PostMapping
    public ProposalResponse Proposal(@RequestBody ProposalRequest proposalRequest) {


        if(proposalRequest.getAge() == '0' || proposalRequest.getExercisePurpose() == '0'){
            throw new IllegalArgumentException();
        }
        return new ProposalResponse(
                "SUCCESS",
                    new ProposalResponse.Data(
                            12L,
                            "PENDING",
                            "20251209",
                            "20251231"
                    )
        );
    }

    @GetMapping("/trainer/read")
    public TrainerReceivedProposalResponse trainerRead(@RequestParam String status, @RequestParam String region) {

        if (status == null || status.isBlank() || region == null || region.isBlank()) {
            throw new IllegalArgumentException();
        }

        return new TrainerReceivedProposalResponse(
                List.of(new TrainerReceivedProposalResponse.Content(
                        12345L, "헬린이1", "서울", 20
                ))
        );
    }

    @GetMapping("/trainer/proposaldetail")
    public TrainerReceivedProposalDetailResponse trainerProposalDetail(@RequestParam String requestid) {

        if(requestid == null || requestid.isBlank()){
            throw new IllegalArgumentException();
        }


        return new TrainerReceivedProposalDetailResponse(
                '1',
                '2',
                '1',
                "도내동",
                "male",
                '1',
                '3',
                '1'
        );
    }


}
