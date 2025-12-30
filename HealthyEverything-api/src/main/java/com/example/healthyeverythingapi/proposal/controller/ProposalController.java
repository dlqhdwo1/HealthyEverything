package com.example.healthyeverythingapi.proposal.controller;

import com.example.healthyeverythingapi.proposal.dto.ProposalRequest;
import com.example.healthyeverythingapi.proposal.dto.ProposalResponse;
import com.example.healthyeverythingapi.proposal.dto.TrainerReceivedProposalDetailResponse;
import com.example.healthyeverythingapi.proposal.dto.TrainerReceivedProposalResponse;
import com.example.healthyeverythingapi.proposal.service.ProposalService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/proposal")
@RequiredArgsConstructor
public class ProposalController {

    private final ProposalService proposalService;

    @PostMapping
    public ProposalResponse Proposal(@RequestBody ProposalRequest proposalRequest) {
        return proposalService.createProposal(proposalRequest);
    }

    @GetMapping("/trainer/read")
    public TrainerReceivedProposalResponse trainerRead(@RequestParam String status, @RequestParam String region) {
        return proposalService.getTrainerProposals(status, region);
    }

    @GetMapping("/trainer/proposaldetail")
    public TrainerReceivedProposalDetailResponse trainerProposalDetail(@RequestParam String requestid) {
        return proposalService.getProposalDetail(requestid);
    }
}
