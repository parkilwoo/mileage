package com.example.mileage.common;

public enum Type {
    REVIEW("REVIEW");

    private final String value;
    Type(String value) { this.value = value; }
    public String value() { return value; }
}
