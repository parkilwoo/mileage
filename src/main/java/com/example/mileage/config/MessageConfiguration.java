package com.example.mileage.config;

import com.example.mileage.common.Utils;
import net.rakugakibox.util.YamlResourceBundle;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.yaml.snakeyaml.Yaml;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Message YML 파일 설정을 위한 클래스
 * 여기서는 메세지 다국화는 제외한다.
 */
@Configuration
public class MessageConfiguration {

    /**
     * Default Locale 설정 (한국어)
     * @return
     */
    @Bean
    public LocaleResolver localeResolver() {
        SessionLocaleResolver slr = new SessionLocaleResolver();
        slr.setDefaultLocale(Locale.KOREA);
        return slr;
    }

    /**
     * message source 생성
     * Spring Boot에서는 자동으로 messageSource가 Bean으로 등록됨
     * But, properties 파일이 아닌 YML파일로 사용할거라 설정
     *
     * setAlwaysUseMessageFormat -> MessageFormat을 전체 메시지에 적용할지 여부(T/F)
     * setUseCodeAsDefaultMessage -> 감지된 Locale파일이 없을때 (T: System Local 사용 / F: messages.properties 사용)
     * setFallbackToSystemLocale -> 메시지를 찾지 못했을때, 예외처리 대신 메시지 반환 여부(T/F)
     *
     * @param basename message source path
     * @param encoding message source encoding
     * @return yaml message source
     */
    @Bean("messageSource")
    public MessageSource messageSource(
            @Value("${spring.messages.basename}") String basename,
            @Value("${spring.messages.encoding}") String encoding
    ) {
        YamlMessageSource ms = new YamlMessageSource();
        ms.setBasename(basename);
        ms.setDefaultEncoding(encoding);
        ms.setAlwaysUseMessageFormat(true);
        ms.setUseCodeAsDefaultMessage(true);
        ms.setFallbackToSystemLocale(true);
        return ms;
    }

    /**
     * getMessage 메서드를 제공하는 MessageSource를 쉽게 접근하게 해주는 helper class
     * @param messageSource
     * @return
     */
    @Bean
    public MessageSourceAccessor messageSourceAccessor(MessageSource messageSource) {
        MessageSourceAccessor messageSourceAccessor = new MessageSourceAccessor(messageSource);
        Utils.setMessageSourceAccessor(messageSourceAccessor); // Utils 클래스에 세팅
        return messageSourceAccessor;
    }
}

/**
 * yaml message source binding
 */
class YamlMessageSource extends ResourceBundleMessageSource {
    @Override
    protected ResourceBundle doGetBundle(String basename, Locale locale) throws MissingResourceException {
        return ResourceBundle.getBundle(basename, locale, YamlResourceBundle.Control.INSTANCE);
    }
}
