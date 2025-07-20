package com.kasino.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kasino.dto.GameStatsDto;
import com.kasino.service.AdminGameStatsService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminStatsController {

    private final AdminGameStatsService adminGameStatsService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/gamestats")
    public ResponseEntity<List<GameStatsDto>> getAllGameStats() {
        List<GameStatsDto> stats = adminGameStatsService.getAllGameStats();
        return ResponseEntity.ok(stats);
    }
}

