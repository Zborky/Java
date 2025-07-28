package com.kasino.model;

import java.util.ArrayList;
import java.util.List;

public class PokerGameState {
    public enum GameStage {
        PRE_FLOP, FLOP, TURN, RIVER, SHOWDOWN
    }

    private List<String> deck;
    private List<String> playerCards;
    private List<String> botCards;
    private List<String> communityCards = new ArrayList<>();
    private double pot;
    private double currentBet;
    private double betAmount;
    private double amountWon;
    private String username;
    private GameStage stage = GameStage.PRE_FLOP;
    private boolean playerFolded = false;

    // --- Gettre a settre ---

    public List<String> getDeck() {
        return deck;
    }

    public void setDeck(List<String> deck) {
        this.deck = deck;
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

    public double getPot() {
        return pot;
    }

    public void setPot(double pot) {
        this.pot = pot;
    }

    public double getCurrentBet() {
        return currentBet;
    }

    public void setCurrentBet(double currentBet) {
        this.currentBet = currentBet;
    }

    public double getBetAmount() {
        return betAmount;
    }

    public void setBetAmount(double betAmount) {
        this.betAmount = betAmount;
    }

    public double getAmountWon() {
        return amountWon;
    }

    public void setAmountWon(double amountWon) {
        this.amountWon = amountWon;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public GameStage getStage() {
        return stage;
    }

    public void setStage(GameStage stage) {
        this.stage = stage;
    }

    public boolean isPlayerFolded() {
        return playerFolded;
    }

    public void setPlayerFolded(boolean playerFolded) {
        this.playerFolded = playerFolded;
    }
}
