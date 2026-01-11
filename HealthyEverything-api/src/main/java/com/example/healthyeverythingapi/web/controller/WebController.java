package com.example.healthyeverythingapi.web.controller;

import com.example.healthyeverythingapi.search.domain.Trainer;
import com.example.healthyeverythingapi.search.repository.TrainerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class WebController {

    private final TrainerRepository trainerRepository;

    @GetMapping("/")
    public String home(Model model) {
        List<Trainer> trainers = trainerRepository.findAll();
        model.addAttribute("trainers", trainers);
        model.addAttribute("keyword", "");
        return "index";
    }

    @GetMapping("/search")
    public String search(@RequestParam(required = false, defaultValue = "") String keyword, Model model) {
        List<Trainer> trainers;
        if (keyword.isEmpty()) {
            trainers = trainerRepository.findAll();
        } else {
            trainers = trainerRepository.findByNameContaining(keyword);
        }
        model.addAttribute("trainers", trainers);

        model.addAttribute("keyword", keyword);
        return "index";
    }

    @GetMapping("/signup")
    public String signupPage() {
        return "signup";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }
}