package com.example.mileage.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;

import java.util.Locale;

/**
 * Util Class
 */
@Slf4j
public class Utils {
    private static MessageSourceAccessor messageSourceAccessor;

    /**
     * ApplicationContext에서 Bean객체를 얻는 Method (DL)
     *
     * @param beanName 얻으려는 Bean객체 이름
     * @return bean object
     */
    public static Object getBean(String beanName) {
        ApplicationContext applicationContext = ApplicationContextProvider.getApplicationContext();
        return applicationContext.getBean(beanName);
    }

//    public static String getMessageSource(String code) {
//        final MessageSource messageSource = (MessageSource) getBean("messageSource");
//        return messageSource.getMessage(code, null, Locale.getDefault());
//    }

    public static String getMessage(String code) {
        return messageSourceAccessor.getMessage(code);
    }
    public static void setMessageSourceAccessor(MessageSourceAccessor messageSourceAccessor) {
        Utils.messageSourceAccessor = messageSourceAccessor;
    }
}
