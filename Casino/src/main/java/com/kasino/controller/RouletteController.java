package com.kasino.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kasino.dto.RouletteRequestDto;
import com.kasino.dto.RouletteResultDto;
import com.kasino.service.RouletteService;

@RestController
@RequestMapping("/api/roulette")
@CrossOrigin(origins = "*")
public class RouletteController {

    @Autowired
    private RouletteService rouletteService;

    @PostMapping("/start")
    public RouletteResultDto startGame(@RequestBody RouletteRequestDto request, Principal principal) {
        String username = principal.getName(); // Fing name to login User
        return rouletteService.startGame(username, request);
    }
}
