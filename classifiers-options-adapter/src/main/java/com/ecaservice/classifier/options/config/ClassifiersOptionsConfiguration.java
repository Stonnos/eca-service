package com.ecaservice.classifier.options.config;

import com.ecaservice.classifier.options.adapter.ClassifierOptionsAdapter;
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
@ComponentScan(basePackageClasses = {AbstractClassifierMapper.class, ClassifierOptionsAdapter.class})
@EnableConfigurationProperties(ClassifiersOptionsConfig.class)
public class ClassifiersOptionsConfiguration {
}
