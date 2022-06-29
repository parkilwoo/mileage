package com.example.mileage.common.exception;

import lombok.Getter;

/**
 * Custom Exception Enum
 * 커스텀 익셉션 코드 정의를 위한 Enum
 */
@Getter
public enum CustomExceptionEnum {
    VALIDATE_EXCEPTION("800"),
    BUSINESS_EXCEPTION("900");
    private final String code;

    CustomExceptionEnum(String code) {
        this.code = code;
    }
}
