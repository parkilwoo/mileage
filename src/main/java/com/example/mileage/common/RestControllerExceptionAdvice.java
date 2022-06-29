package com.example.mileage.common;

import com.example.mileage.common.exception.CustomException;
import com.example.mileage.common.exception.CustomExceptionEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
/**
 * RestController Exception Advice
 */
public class RestControllerExceptionAdvice {
    private final Result result = new Result();

    @ResponseBody
    @ExceptionHandler({MethodArgumentNotValidException.class})
    public Result unauthorizedUserExceptionHandler(MethodArgumentNotValidException exception) {
        final String VALIDATE_MSG = exception.getBindingResult().getFieldError().getDefaultMessage();
        result.setFail(CustomExceptionEnum.VALIDATE_EXCEPTION.getCode(), VALIDATE_MSG);
        return result;
    }

    @ResponseBody
    @ExceptionHandler(CustomException.class)
    public Result unauthorizedUserExceptionHandler(CustomException exception) {
        result.setFail(exception.getERR_CODE(), exception.getERR_MSG());
        return result;
    }


    @ResponseBody
    @ExceptionHandler(Exception.class)
    public Result unauthorizedUserExceptionHandler(Exception exception) {
        log.error(exception.getMessage());
        result.setFail();
        result.setMsg(exception.getMessage());
        return result;
    }
}
