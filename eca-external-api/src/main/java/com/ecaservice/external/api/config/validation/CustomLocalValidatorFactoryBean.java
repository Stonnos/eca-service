package com.ecaservice.external.api.config.validation;

import com.ecaservice.external.api.dto.annotations.DataURL;
import com.ecaservice.external.api.validation.DataURLValidator;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.HibernateValidatorConfiguration;
import org.hibernate.validator.cfg.ConstraintMapping;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import javax.validation.Configuration;

/**
 * Custom local validator factory bean.
 *
 * @author Roman Batygin
 */
@RequiredArgsConstructor
public class CustomLocalValidatorFactoryBean extends LocalValidatorFactoryBean {

    @Override
    protected void postProcessConfiguration(Configuration<?> configuration) {
        super.postProcessConfiguration(configuration);
        HibernateValidatorConfiguration hibernateConfiguration = (HibernateValidatorConfiguration) configuration;
        ConstraintMapping constraintMapping = hibernateConfiguration.createConstraintMapping();
        constraintMapping.constraintDefinition(DataURL.class).validatedBy(DataURLValidator.class);
        hibernateConfiguration.addMapping(constraintMapping);
    }
}
