package com.kasino.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kasino.dto.BlackJackRequestDto;
import com.kasino.dto.BlackJackResultDto;
import com.kasino.service.BlackJackService;

@RestController
@RequestMapping("/api/blackjack")
@CrossOrigin
public class BlackJackController {

    @Autowired
    private BlackJackService blackJackService;

    @PostMapping("/start")
    public BlackJackResultDto startGame(@RequestBody BlackJackRequestDto request, Principal principal) {
        String username = principal.getName(); // GEt actual logged user
        return blackJackService.startGame(username, request);
    }

    @PostMapping("/hit/{gameId}")
    public BlackJackResultDto hit(@PathVariable String gameId, Principal principal) {
        String username = principal.getName();
        return blackJackService.hit(username, gameId);
    }

    @PostMapping("/stand/{gameId}")
    public BlackJackResultDto stand(@PathVariable String gameId, Principal principal) {
        String username = principal.getName();
        return blackJackService.stand(username, gameId);
    }
}
