package com.example.mileage.common;

/**
 * Request Parameter Action Enum
 */
public enum Action {
    ADD("ADD"),
    MOD("MOD"),
    DELETE("DELETE");

    private final String value;
    Action(String value) { this.value = value; }
    public String value() { return value; }
}
