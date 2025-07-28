package com.kasino.dto;

import java.util.List;

public class PokerResultDto {
    private List<String> playerCards;
    private List<String> botCards;
    private List<String> communityCards;
    private String playerHandRank;
    private String botHandRank;
    private double balance;
    private String message;
    private boolean gameOver;
    private String gameId;
    private double amountWon;
    private double pot;

    // ✅ Doplnený konštruktor
    public PokerResultDto(String message, double balance, boolean gameOver) {
        this.message = message;
        this.balance = balance;
        this.gameOver = gameOver;
    }

    public PokerResultDto() {
    }

    public double getPot(){
        return pot;
    }

    public void setPot(double pot){
        this.pot = pot;
    }

    public List<String> getPlayerCards() {
        return playerCards;
    }

    public void setPlayerCards(List<String> playerCards) {
        this.playerCards = playerCards;
    }

    public List<String> getBotCards() {
        return botCards;
    }

    public void setBotCards(List<String> botCards) {
        this.botCards = botCards;
    }

    public List<String> getCommunityCards() {
        return communityCards;
    }

    public void setCommunityCards(List<String> communityCards) {
        this.communityCards = communityCards;
    }

    public String getPlayerHandRank() {
        return playerHandRank;
    }

    public void setPlayerHandRank(String playerHandRank) {
        this.playerHandRank = playerHandRank;
    }

    public String getBotHandRank() {
        return botHandRank;
    }

    public void setBotHandRank(String botHandRank) {
        this.botHandRank = botHandRank;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public double getAmountWon() {
        return amountWon;
    }

    public void setAmountWon(double amountWon) {
        this.amountWon = amountWon;
    }
}
