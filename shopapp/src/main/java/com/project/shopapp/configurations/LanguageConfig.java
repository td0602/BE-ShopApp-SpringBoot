package com.project.shopapp.configurations;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;

@Configuration
public class LanguageConfig {
//    noi chua tep da ngon ngu
    @Bean
    public MessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("i18n.messages"); // tên cơ sở các tập tài liệu ngôn ngữ
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }
}
