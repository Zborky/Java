package com.kasino.dto;

public class DiceGameRequestDto {
    private String playerName;
    private int betAmount;

    public String getPlayerName(){
        return playerName;
    }

    public void setPlayerName(String playerName){
        this.playerName = playerName;
    }

    public int getBetAmount(){
        return betAmount;
    }

    public void setBetAmount(int betAmount){
        this.betAmount = betAmount;
    }
}
