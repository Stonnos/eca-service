package com.ecaservice.auto.test.service;

import com.ecaservice.auto.test.config.AutoTestsProperties;
import com.ecaservice.auto.test.model.ClassifierTestDataModel;
import com.ecaservice.test.common.service.AbstractTestDataProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service to provide classifiers test data
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
public class ClassifierTestDataProvider extends AbstractTestDataProvider<ClassifierTestDataModel> {

    private final AutoTestsProperties autoTestsProperties;

    /**
     * Constructor with spring dependency injection.
     *
     * @param autoTestsProperties - auto tests properties
     */
    public ClassifierTestDataProvider(AutoTestsProperties autoTestsProperties) {
        super(ClassifierTestDataModel.class);
        this.autoTestsProperties = autoTestsProperties;
    }

    @Override
    protected String getTestDataPath() {
        return autoTestsProperties.getClassifiersDataPath();
    }
}
