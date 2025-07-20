package com.kasino.dto;

public class SpinResultDto {
    private String[][] grid;
    private double balance;
    private String message;

    //Getters and Setters
    public String[][] getGrid(){
        return grid;
    }

    public void setSymbols(String[][] grid){
        this.grid = grid;
    }

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
}

