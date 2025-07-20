package com.kasino.dto;

public class RouletteResultDto {
    private double balance;
    private String message;
    private boolean gameOver;
    private String gameId;
    private boolean win;
    private double amountWon;
    private int winningNumber;
    private String winningColor;

    public double getBalance(){
        return balance;
    }

    public void setBalance(double balance){
        this.balance = balance;
    }

    public String getMessage(){
        return message;
    }
    public void setMessage(String message){
        this.message = message;
    }

    public boolean isGameOver(){
        return gameOver;
    }

    public void setGameOver(boolean gameOver){
        this.gameOver = gameOver;
    }

    public String getGameId(){
        return gameId;
    }

    public void setGameId(String gameId){
        this.gameId = gameId;
    }

    public boolean isWin(){
        return win;
    }

    public void setWin(boolean win){
        this.win = win;
    }

    public double getAmountWon(){
        return amountWon;
    }

    public void setAmountWon(double amountWon){
        this.amountWon = amountWon;
    }

    public int getWinningNumber(){
        return winningNumber;
    }

    public void setWinningNumber(int winningNumber){
        this.winningNumber = winningNumber;

    }
    public String getWinningColor(){
        return winningColor;
    }

    public void setWinningColor(String winningColor){
        this.winningColor = winningColor;
    }

}
