package com.example.healthyeverythingapi.proposal.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "proposals")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Proposal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private char exercisePurpose;

    @Column(nullable = false)
    private char exerciseExperience;

    @Column(nullable = false)
    private char lessonCount;

    @Column(nullable = false, length = 100)
    private String lessonLocate;

    @Column(nullable = false, length = 10)
    private String gender;

    @Column(nullable = false)
    private char age;

    @Column(nullable = false)
    private char hopeRequest;

    @Column(nullable = false, length = 20)
    private String status;

    @Column(length = 20)
    private String createdAt;

    @Column(length = 20)
    private String expiresAt;

    private String username;

    private String region;
}
