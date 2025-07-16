package com.kasino.dto;

public class RouletteRequestDto {
 private double betAmount;
 private String betType;
 private int chosenNumber;
 
 public double getBetAmount(){
    return betAmount;
 }

 public void setBetAmount(double betAmount){
    this.betAmount = betAmount;
 }
 public String getBetType(){
    return betType;
 }
 public void setBetType(String betType){
    this.betType = betType;
 }

 public int getChosenNumber(){
    return chosenNumber;
 }

 public void setChosenNumber(int chosenNumber){
    this.chosenNumber = chosenNumber;

 }

}
