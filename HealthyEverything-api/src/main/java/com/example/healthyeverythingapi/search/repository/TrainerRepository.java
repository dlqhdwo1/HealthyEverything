package com.example.healthyeverythingapi.search.repository;

import com.example.healthyeverythingapi.search.domain.Trainer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TrainerRepository extends JpaRepository<Trainer, Long> {

    Optional<Trainer> findBySearchKeyword(String searchKeyword);

    List<Trainer> findByNameContaining(String name);
}
