package de.x1c1b.taskcare.api.config;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedOrigins("*");
    }

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

    @Override
    public Validator getValidator() {
        return localValidatorFactoryBean(reloadableResourceBundleMessageSource());
    }
}
