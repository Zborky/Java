package com.kasino.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.kasino.repository.UserRepository;
import com.kasino.service.AdminGameStatsService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class AdminPageController {

    private final UserRepository userRepository;
    private final AdminGameStatsService adminGameStatsService;
    @GetMapping("/admin")
    public String adminPage(Model model) {
        model.addAttribute("users", userRepository.findAll());
        model.addAttribute("gameStats",adminGameStatsService.getAllGameStats());
        return "admin";  // admin.html použije atribút "users"
    }
}
