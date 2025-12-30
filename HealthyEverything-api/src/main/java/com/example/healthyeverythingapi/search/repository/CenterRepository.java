package com.example.healthyeverythingapi.search.repository;

import com.example.healthyeverythingapi.search.domain.Center;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CenterRepository extends JpaRepository<Center, Long> {

    List<Center> findByNameContaining(String keyword);
}
