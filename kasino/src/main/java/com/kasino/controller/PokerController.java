package com.kasino.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kasino.dto.PokerActionRequestDto;
import com.kasino.dto.PokerRequestDto;
import com.kasino.dto.PokerResultDto;
import com.kasino.service.PokerService;

@RestController
@RequestMapping("/api/poker")
public class PokerController {

    @Autowired
    private PokerService pokerService;

    @PostMapping("/start")
    public PokerResultDto startGame(@RequestBody PokerRequestDto request, Principal principal) {
        PokerResultDto result = new PokerResultDto();

        try {
            if (principal == null) {
                result.setMessage("Musíte byť prihlásený, aby ste mohli hrať.");
                result.setGameOver(true);
                result.setBalance(0.0);
                result.setAmountWon(0.0);
                return result;
            }

            String username = principal.getName();
            return pokerService.startGame(username, request);

        } catch (Exception e) {
            e.printStackTrace();
            result.setMessage("Internal server error: " + e.getMessage());
            result.setGameOver(true);
            result.setBalance(0.0);
            result.setAmountWon(0.0);
            return result;
        }
    }

    // Nový endpoint pre pokerové akcie ako Raise, Call, Fold
    @PostMapping("/action/{gameId}")
public PokerResultDto performAction(@PathVariable String gameId,
                                    @RequestBody PokerActionRequestDto actionRequest,
                                    Principal principal) {
    PokerResultDto result = new PokerResultDto();

    try {
        if (principal == null) {
            result.setMessage("Musíte byť prihlásený, aby ste mohli hrať.");
            result.setGameOver(true);
            result.setBalance(0.0);
            result.setAmountWon(0.0);
            return result;
        }

        String username = principal.getName();
        double raiseAmount = actionRequest.getRaiseAmount() == null ? 0.0 : actionRequest.getRaiseAmount();

        return pokerService.playerAction(username, gameId, actionRequest.getAction(), raiseAmount);

    } catch (Exception e) {
        e.printStackTrace();
        result.setMessage("Internal server error: " + e.getMessage());
        result.setGameOver(true);
        result.setBalance(0.0);
        result.setAmountWon(0.0);
        return result;
    }
}
}
