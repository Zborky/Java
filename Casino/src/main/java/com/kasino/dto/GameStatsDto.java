package com.kasino.dto;

import java.time.LocalDateTime;

public class GameStatsDto {
    private String gameName;
    private long totalGamesPlayed;
    private double totalPlayerWins;
    private double totalCasinoProfit;
    private LocalDateTime lastPlayed;

    public GameStatsDto(String gameName, long totalGamesPlayed,double totalPlayerWins,double totalCasinoProfit,LocalDateTime lastPlayed){
        this.gameName = gameName;
        this.totalGamesPlayed = totalGamesPlayed;
        this.totalPlayerWins = totalPlayerWins;
        this.totalCasinoProfit = totalCasinoProfit;
        this.lastPlayed = lastPlayed;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public long getTotalGamesPlayed() {
        return totalGamesPlayed;
    }

    public void setTotalGamesPlayed(long totalGamesPlayed) {
        this.totalGamesPlayed = totalGamesPlayed;
    }

    public double getTotalPlayerWins() {
        return totalPlayerWins;
    }

    public void setTotalPlayerWins(double totalPlayerWins) {
        this.totalPlayerWins = totalPlayerWins;
    }

    public double getTotalCasinoProfit() {
        return totalCasinoProfit;
    }

    public void setTotalCasinoProfit(double totalCasinoProfit) {
        this.totalCasinoProfit = totalCasinoProfit;
    }

    public LocalDateTime getLastPlayed() {
        return lastPlayed;
    }

    public void setLastPlayed(LocalDateTime lastPlayed) {
        this.lastPlayed = lastPlayed;
    }
}
