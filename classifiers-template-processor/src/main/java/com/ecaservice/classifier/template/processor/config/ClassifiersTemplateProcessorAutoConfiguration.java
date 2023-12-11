package com.ecaservice.classifier.template.processor.config;

import com.ecaservice.core.form.template.annotation.EnableFormTemplates;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Classifiers template processor auto configuration.
 *
 * @author Roman Batygin
 */
@Configuration
@EnableFormTemplates
@ComponentScan({"com.ecaservice.classifier.template.processor"})
@EnableConfigurationProperties(ClassifiersTemplateProperties.class)
public class ClassifiersTemplateProcessorAutoConfiguration {
}
