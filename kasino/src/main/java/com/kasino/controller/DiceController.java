package com.kasino.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kasino.dto.DiceGameRequestDto;
import com.kasino.dto.DiceGameResultDto;
import com.kasino.service.DiceService;

@RestController
@RequestMapping("/api/dice")
public class DiceController {

    @Autowired
    private DiceService diceService;

    @PostMapping("/start")
    public DiceGameResultDto startGame(@RequestBody DiceGameRequestDto request, Principal principal) {
        DiceGameResultDto result = new DiceGameResultDto();

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
            return diceService.startGame(username, request);

        } catch (Exception e) {
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
