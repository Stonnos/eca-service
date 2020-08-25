package com.ecaservice.classifier.options.configuration;

import com.ecaservice.classifier.options.mapping.AbstractClassifierMapper;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Classifiers options configuration class.
 *
 * @author Roman Batygin
 */
@Configuration
@ComponentScan(basePackageClasses = AbstractClassifierMapper.class)
@EnableConfigurationProperties(ClassifiersOptionsConfig.class)
public class ClassifiersOptionsConfiguration {
}
