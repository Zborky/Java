package com.kasino.dto;

public class SlotResult {
    private String[] slots;
    private boolean win;
    private int payout;
    private String message;

    public SlotResult(String[] slots, boolean win, int payout, String message) {
        this.slots = slots;
        this.win = win;
        this.payout = payout;
        this.message = message;
    }

    public String[] getSlots() {
        return slots;
    }

    public boolean isWin() {
        return win;
    }

    public int getPayout() {
        return payout;
    }

    public String getMessage() {
        return message;
    }
}
