package com.example.mileage.common.exception;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Custom Exception 만들기 위한 Class
 */
@Getter
public class CustomException extends Exception {

    private final String ERR_CODE;
    private final String ERR_MSG;

    @NoArgsConstructor
    public static class Builder implements CustomExceptionBuilder {
        private String errCode;
        private String errMsg;

        @Override
        public CustomExceptionBuilder code(String code) {
            this.errCode = code;
            return this;
        }

        @Override
        public CustomExceptionBuilder message(String message) {
            this.errMsg = message;
            return this;
        }


        @Override
        public CustomException build() {
            return new CustomException(this);
        }
    }

    private CustomException(Builder builder) {
        super(builder.errMsg);
        this.ERR_CODE = builder.errCode;
        this.ERR_MSG = builder.errMsg;
    }
}
