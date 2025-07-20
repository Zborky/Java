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
    RouletteResultDto result = new RouletteResultDto();

    try {
        if (principal == null) {
            result.setMessage("Musíte byť prihlásený, aby ste mohli hrať.");
            result.setGameOver(true);
            result.setBalance(0.0);
            result.setWin(false);
            result.setAmountWon(0.0);
            return result;
        }

        String username = principal.getName();
        return rouletteService.startGame(username, request);

    } catch (Exception e) {
        // Here we write Error from log
        e.printStackTrace();
        result.setMessage("Internal server error: " + e.getMessage());
        result.setGameOver(true);
        result.setBalance(0.0);
        result.setWin(false);
        result.setAmountWon(0.0);
        return result;
    }
}
}
