package com.ecaservice.auto.test.service;

import com.ecaservice.auto.test.config.AutoTestsProperties;
import com.ecaservice.auto.test.model.ExperimentTestDataModel;
import com.ecaservice.test.common.service.AbstractTestDataProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service to provide experiment test data
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
public class ExperimentTestDataProvider extends AbstractTestDataProvider<ExperimentTestDataModel> {

    private final AutoTestsProperties autoTestsProperties;

    /**
     * Constructor with spring dependency injection.
     *
     * @param autoTestsProperties - auto tests properties
     */
    public ExperimentTestDataProvider(AutoTestsProperties autoTestsProperties) {
        super(ExperimentTestDataModel.class);
        this.autoTestsProperties = autoTestsProperties;
    }

    @Override
    protected String getTestDataPath() {
        return autoTestsProperties.getExperimentsDataPath();
    }
}
