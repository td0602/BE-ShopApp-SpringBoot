package com.project.shopapp.components;

import com.project.shopapp.utils.WebUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.LocaleResolver;

import java.util.Locale;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class LocalizationUtils {
    private final MessageSource messageSource;
    private final LocaleResolver localeResolver;

//    @Bean
    public String getLocalizedMessage(String messageKey, Object ... params) {
        Locale locale = localeResolver.resolveLocale(WebUtils.getCurrentRequest()); // vi hay en
        return messageSource.getMessage(messageKey, params, locale);
    }
}
