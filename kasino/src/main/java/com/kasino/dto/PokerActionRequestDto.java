package com.kasino.dto;

public class PokerActionRequestDto {
    private String action;  // "RAISE", "CALL", "FOLD"
    private Double raiseAmount; // môže byť null ak nie je raise

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Double getRaiseAmount() {
        return raiseAmount;
    }

    public void setRaiseAmount(Double raiseAmount) {
        this.raiseAmount = raiseAmount;
    }
}
