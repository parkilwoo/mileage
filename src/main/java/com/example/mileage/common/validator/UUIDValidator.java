package com.example.mileage.common.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.UUID;

/**
 * UuidValidator Class
 */
public class UUIDValidator implements ConstraintValidator<UUIDValid, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if(value == null || value.length() != 36) return false;
        boolean result;
        try {
            UUID.fromString(value);
            result = true;
        }
        catch (IllegalArgumentException e) {
            result = false;
        }
        return result;
    }
}
