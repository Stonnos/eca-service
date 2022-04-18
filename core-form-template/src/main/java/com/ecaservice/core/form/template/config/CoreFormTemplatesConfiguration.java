package com.ecaservice.core.form.template.config;

import com.ecaservice.core.form.template.entity.FormTemplateEntity;
import com.ecaservice.core.form.template.repository.FormTemplateRepository;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Core form templates configuration class.
 *
 * @author Roman Batygin
 */
@Configuration
@ComponentScan({"com.ecaservice.core.form.template"})
@EntityScan(basePackageClasses = FormTemplateEntity.class)
@EnableJpaRepositories(basePackageClasses = FormTemplateRepository.class)
public class CoreFormTemplatesConfiguration {
}
