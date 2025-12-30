package com.example.healthyeverythingapi.search.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "centers")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Center {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 255)
    private String profileImage;

    @Column(length = 100)
    private String price;

    @Column(length = 255)
    private String info;

    @Column(length = 100)
    private String time;

    @Column(length = 100)
    private String locate;
}
