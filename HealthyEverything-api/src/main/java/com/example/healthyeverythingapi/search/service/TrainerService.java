package com.example.healthyeverythingapi.search.service;

import com.example.healthyeverythingapi.search.domain.Trainer;
import com.example.healthyeverythingapi.search.repository.TrainerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TrainerService {

    private final TrainerRepository trainerRepository;

    public Optional<Trainer> findBySearchKeyword(String keyword) {
        return trainerRepository.findBySearchKeyword(keyword);
    }

    public Trainer findById(Long id) {
        return trainerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("TRAINER_NOT_FOUND"));
    }

    public List<Trainer> findAll() {
        return trainerRepository.findAll();
    }

    public List<Trainer> findByNameContaining(String name) {
        return trainerRepository.findByNameContaining(name);
    }
}
