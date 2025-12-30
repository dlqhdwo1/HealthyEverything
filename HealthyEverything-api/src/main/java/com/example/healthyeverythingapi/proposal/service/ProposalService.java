package com.example.healthyeverythingapi.proposal.service;

import com.example.healthyeverythingapi.proposal.domain.Proposal;
import com.example.healthyeverythingapi.proposal.dto.ProposalRequest;
import com.example.healthyeverythingapi.proposal.dto.ProposalResponse;
import com.example.healthyeverythingapi.proposal.dto.TrainerReceivedProposalDetailResponse;
import com.example.healthyeverythingapi.proposal.dto.TrainerReceivedProposalResponse;
import com.example.healthyeverythingapi.proposal.repository.ProposalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProposalService {

    private final ProposalRepository proposalRepository;

    @Transactional
    public ProposalResponse createProposal(ProposalRequest request) {
        if (request.getAge() == '0' || request.getExercisePurpose() == '0') {
            throw new IllegalArgumentException("INVALID_REQUEST");
        }

        Proposal proposal = Proposal.builder()
                .exercisePurpose(request.getExercisePurpose())
                .exerciseExperience(request.getExerciseExperience())
                .lessonCount(request.getLessonCount())
                .lessonLocate(request.getLessonLocate())
                .gender(request.getGender())
                .age(request.getAge())
                .hopeRequest(request.getHopeRequest())
                .status("PENDING")
                .createdAt(LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE))
                .expiresAt(LocalDate.now().plusDays(30).format(DateTimeFormatter.BASIC_ISO_DATE))
                .build();

        Proposal saved = proposalRepository.save(proposal);

        return new ProposalResponse(
                "SUCCESS",
                new ProposalResponse.Data(
                        saved.getId(),
                        saved.getStatus(),
                        saved.getCreatedAt(),
                        saved.getExpiresAt()
                )
        );
    }

    @Transactional(readOnly = true)
    public TrainerReceivedProposalResponse getTrainerProposals(String status, String region) {
        if (status == null || status.isBlank() || region == null || region.isBlank()) {
            throw new IllegalArgumentException("INVALID_REQUEST");
        }

        List<Proposal> proposals = proposalRepository.findByStatusAndRegion(status, region);

        List<TrainerReceivedProposalResponse.Content> contents = proposals.stream()
                .map(p -> new TrainerReceivedProposalResponse.Content(
                        p.getId(),
                        p.getUsername(),
                        p.getLessonLocate(),
                        Character.getNumericValue(p.getLessonCount()) * 10
                ))
                .toList();

        return new TrainerReceivedProposalResponse(contents);
    }

    @Transactional(readOnly = true)
    public TrainerReceivedProposalDetailResponse getProposalDetail(String requestId) {
        if (requestId == null || requestId.isBlank()) {
            throw new IllegalArgumentException("INVALID_REQUEST");
        }

        Long id = Long.parseLong(requestId);
        Proposal proposal = proposalRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("PROPOSAL_NOT_FOUND"));

        return new TrainerReceivedProposalDetailResponse(
                proposal.getExercisePurpose(),
                proposal.getExerciseExperience(),
                proposal.getLessonCount(),
                proposal.getLessonLocate(),
                proposal.getGender(),
                proposal.getAge(),
                proposal.getLessonCount(),
                proposal.getHopeRequest()
        );
    }
}
