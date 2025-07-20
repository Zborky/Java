package com.kasino.dto;
import java.util.List;

public class BlackJackResultDto {
    private List<String> playerCards;
    private List<String> dealerCards;
    private double balance;
    private String message;
    private boolean gameOver;
    private String gameId;
    private int playerScore;
    private int dealerScore;

    // Gettery and Setter
    public List<String> getPlayerCards() { return playerCards; }
    public void setPlayerCards(List<String> playerCards) { this.playerCards = playerCards; }

    public List<String> getDealerCards() { return dealerCards; }
    public void setDealerCards(List<String> dealerCards) { this.dealerCards = dealerCards; }

    public double getBalance() { return balance; }
    public void setBalance(double balance) { this.balance = balance; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public boolean isGameOver() { return gameOver; }
    public void setGameOver(boolean gameOver) { this.gameOver = gameOver; }

    public String getGameId() { return gameId; }
    public void setGameId(String gameId) { this.gameId = gameId; }

    public int getPlayerScore() { return playerScore; }
    public void setPlayerScore(int playerScore) { this.playerScore = playerScore; }

    public int getDealerScore() { return dealerScore; }
    public void setDealerScore(int dealerScore) { this.dealerScore = dealerScore; }
}
