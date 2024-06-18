package com.ecaservice.classifier.template.processor.config;

import com.ecaservice.core.form.template.config.CoreFormTemplatesConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Classifiers template processor auto configuration.
 *
 * @author Roman Batygin
 */
@Configuration
@AutoConfigureAfter(CoreFormTemplatesConfiguration.class)
@ComponentScan({"com.ecaservice.classifier.template.processor"})
@EnableConfigurationProperties(ClassifiersTemplateProperties.class)
public class ClassifiersTemplateProcessorAutoConfiguration {
}
