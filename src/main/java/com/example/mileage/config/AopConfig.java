package com.example.mileage.config;

import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Aspect
@Slf4j
@Component
/**
 * Logging을 위한 AopConfig
 */
public class AopConfig {

    private final StringBuilder sb = new StringBuilder();
    /**
     * 모든 컨트롤러에 Logging 적용
     * @param joinPoint
     * @return Aspect Proxy Object
     * @throws Throwable
     */
    @Before("execution(* com.example.mileage..*Controller.*(..))")
    public void beforeRequests(JoinPoint joinPoint) throws Throwable {

        log.info("Request: {} {}", joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName());
        log.info("Args: \n{}", paramMapToString(joinPoint.getArgs()));
    }

    /**
     * Param Map To String
     * @param paramObject Request Param
     * @return
     */
    private String paramMapToString(Object[] paramObject) {
        sb.setLength(0);
        Arrays.stream(paramObject)
                .forEach(entry -> {
                    if(entry instanceof Map) {
                        Set<?> set = ((Map<?, ?>) entry).keySet();
                        sb.append(set.stream()
                                .map(key -> String.format("%s : %s", key, ((Map<?, ?>) entry).get(key)))
                                .collect(Collectors.joining("\n")));
                    }
                    else {
                        sb.append(entry.toString());
                    }
                });
        return sb.toString();
    }

}
