package com.example.healthyeverythingapi.proposal.repository;

import com.example.healthyeverythingapi.proposal.domain.Proposal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProposalRepository extends JpaRepository<Proposal, Long> {

    List<Proposal> findByStatusAndRegion(String status, String region);
}
