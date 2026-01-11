package com.example.healthyeverythingapi.search.service;

import com.example.healthyeverythingapi.search.domain.Center;
import com.example.healthyeverythingapi.search.repository.CenterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CenterService {

    private final CenterRepository centerRepository;

    public List<Center> findByNameContaining(String keyword) {
        return centerRepository.findByNameContaining(keyword);
    }

    public Center findById(Long id) {
        return centerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("CENTER_NOT_FOUND"));
    }
}