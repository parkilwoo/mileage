package com.example.mileage.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.Objects;

/**
 * API Result를 담당하는 Class
 */
@Data
public class Result {
    private String code;          // result code
    private String msg;           // result message
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Object data;          // result data

    /**
     * Resp Success 세팅(Response Data 있을시)
     *
     * @param data data
     */
    public void setSuccess(Object data) {
        this.msg = MessageSourceToStr.RESULT_SUCCESS_MSG;
        this.code = MessageSourceToStr.RESULT_SUCCESS_CODE;
        if (!Objects.equals(data, null)) this.data = data;
    }

    /**
     * Default Resp Success
     */
    public void setSuccess() {
        this.msg = MessageSourceToStr.RESULT_SUCCESS_MSG;
        this.code = MessageSourceToStr.RESULT_SUCCESS_CODE;
    }


    /**
     * Default Resp Fail(exception)
     */
    public void setFail() {
        this.msg = MessageSourceToStr.RESULT_FAIL_MSG;
        this.code = MessageSourceToStr.RESULT_FAIL_CODE;
    }

    /**
     * Resp Fail(exception)
     */
    public void setFail(String code, String msg) {
        this.msg = msg;
        this.code = code;
    }
}
