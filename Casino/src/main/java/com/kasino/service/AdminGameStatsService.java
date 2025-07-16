package com.kasino.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.kasino.dto.GameStatsDto;
import com.kasino.model.GameStats;
import com.kasino.repository.GameStatsRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminGameStatsService {

    private final GameStatsRepository gameStatsRepository;

    public List<GameStatsDto> getAllGameStats() {
        return gameStatsRepository.findAll().stream()
            .map(gameStats -> new GameStatsDto(
                gameStats.getGameName(),
                gameStats.getTotalGamesPlayed(),
                gameStats.getTotalPlayerWins(),
                gameStats.getTotalCasinoProfit(),
                gameStats.getLastPlayed()))
            .collect(Collectors.toList());
    }

    public void updateStatsAfterGame(String gameName, double playerWinAmount, double casinoProfit) {
        GameStats stats = gameStatsRepository.findById(gameName).orElseGet(() -> {
            GameStats newStats = new GameStats();
            newStats.setGameName(gameName);
            newStats.setTotalGamesPlayed(0);
            newStats.setTotalPlayerWins(0);
            newStats.setTotalCasinoProfit(0);
            newStats.setLastPlayed(LocalDateTime.now());
            return newStats;
        });

        stats.setTotalGamesPlayed(stats.getTotalGamesPlayed() + 1);
        stats.setTotalPlayerWins(stats.getTotalPlayerWins() + playerWinAmount);
        stats.setTotalCasinoProfit(stats.getTotalCasinoProfit() + casinoProfit);
        stats.setLastPlayed(LocalDateTime.now());

        gameStatsRepository.save(stats);
    }
}
