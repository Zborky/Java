package com.example.Eshop.model;

public enum PaymentMethod {
    CARD, COD, BANK;

    public static PaymentMethod from(String s){
        return PaymentMethod.valueOf(s.toUpperCase());
    }
}
