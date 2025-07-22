package com.kasino.dto;

public class DiceGameResultDto{
    private double balance;
    private String message;
    private boolean gameOver;
    private String gameId;
    private boolean win;
    private double amountWon;
    private int dice1;
    private int dice2;
    private int total;

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

    public int getDice1(){
        return dice1;
    }

    public void setDice1(int dice1){
        this.dice1 = dice1;
    }

    public int getDice2(){
        return dice2;
    }
    public void setDice2(int dice2){
        this.dice2 = dice2;
    }

    public int getTotal(){
        return total;
    }
    public void setTotal(int total){
        this.total = total;
    }

}