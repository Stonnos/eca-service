package com.ecaservice.classifier.template.processor.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Classifier template properties.
 *
 * @author Roman Batygin
 */
@Data
@ConfigurationProperties("classifiers.templates")
public class ClassifiersTemplateProperties {

    private static final int DEFAULT_MAXIMUM_FRACTION_DIGITS = 4;

    /**
     * Maximum fraction digits
     */
    private Integer maximumFractionDigits = DEFAULT_MAXIMUM_FRACTION_DIGITS;
}
