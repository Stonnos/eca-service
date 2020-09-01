package com.ecaservice.load.test.model;

import com.ecaservice.classifier.options.model.ClassifierOptions;
import lombok.Builder;
import lombok.Data;
import org.springframework.core.io.Resource;

/**
 * Test data model.
 *
 * @author Roman Batygin
 */
@Data
@Builder
public class TestDataModel {

    /**
     * Training data resource
     */
    private Resource dataResource;

    /**
     * Classifier options
     */
    private ClassifierOptions classifierOptions;
}
