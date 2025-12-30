package com.example.healthyeverythingapi.search.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "trainers")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Trainer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(length = 255)
    private String profileImage;

    @Column(length = 255)
    private String certificate;

    @Column(length = 100)
    private String price;

    @Column(length = 255)
    private String programDescription;

    @Column(length = 100)
    private String gymLocate;

    private Integer rating;

    @Column(length = 100)
    private String searchKeyword;
}
