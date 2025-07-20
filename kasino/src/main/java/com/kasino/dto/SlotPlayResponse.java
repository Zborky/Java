package com.kasino.dto;

public class SlotPlayResponse {
    private String[] symbols;
    private int winnings;

    public SlotPlayResponse(String[] symbols, int winnings) {
        this.symbols = symbols;
        this.winnings = winnings;
    }

    public String[] getSymbols() {
        return symbols;
    }

    public void setSymbols(String[] symbols) {
        this.symbols = symbols;
    }

    public int getWinnings() {
        return winnings;
    }

    public void setWinnings(int winnings) {
        this.winnings = winnings;
    }
}
