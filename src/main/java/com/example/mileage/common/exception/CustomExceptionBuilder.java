package com.example.mileage.common.exception;

/**
 * Custom Exception Builder Interface
 */
public interface CustomExceptionBuilder {
    CustomExceptionBuilder code(String code);
    CustomExceptionBuilder message(String message);
    <T extends Exception> T build();
}
