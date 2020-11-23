package com.ecaservice.external.api.config.validation;

import lombok.RequiredArgsConstructor;
import org.hibernate.validator.HibernateValidatorConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.boot.validation.MessageInterpolatorFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

/**
 * Validator configuration class.
 *
 * @author Roman Batygin
 */
@AutoConfigureBefore(ValidationAutoConfiguration.class)
@ConditionalOnClass(HibernateValidatorConfiguration.class)
@Configuration
@RequiredArgsConstructor
public class ValidatorConfiguration {

    /**
     * Creates local validator factory bean.
     *
     * @return local validator factory bean
     */
    @Primary
    @Bean
    public LocalValidatorFactoryBean localValidatorFactoryBean() {
        CustomLocalValidatorFactoryBean factoryBean = new CustomLocalValidatorFactoryBean();
        MessageInterpolatorFactory interpolatorFactory = new MessageInterpolatorFactory();
        factoryBean.setMessageInterpolator(interpolatorFactory.getObject());
        return factoryBean;
    }
}
