package com.example.Eshop.model;

public enum ShippingMethod {
    COURIER, PICKUP;

    public static ShippingMethod from(String s){
        return ShippingMethod.valueOf(s.toUpperCase());
    }
}
