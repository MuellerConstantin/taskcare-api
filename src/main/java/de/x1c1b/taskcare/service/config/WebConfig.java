package de.x1c1b.taskcare.service.config;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@Configuration
public class WebConfig {

    @Bean
    @Primary
    MessageSource reloadableResourceBundleMessageSource() {

        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();

        messageSource.setBasename("classpath:message/validation");
        messageSource.setDefaultEncoding("UTF-8");

        return messageSource;
    }

    @Bean
    @Primary
    LocalValidatorFactoryBean localValidatorFactoryBean(MessageSource messageSource) {

        LocalValidatorFactoryBean localValidatorFactoryBean = new LocalValidatorFactoryBean();
        localValidatorFactoryBean.setValidationMessageSource(messageSource);
        return localValidatorFactoryBean;
    }
}
