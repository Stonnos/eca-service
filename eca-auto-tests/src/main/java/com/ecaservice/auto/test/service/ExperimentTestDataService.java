package com.ecaservice.auto.test.service;

import com.ecaservice.auto.test.config.AutoTestsProperties;
import com.ecaservice.auto.test.model.ExperimentTestDataModel;
import com.ecaservice.test.common.service.AbstractTestDataLoader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service to read test requests from resources.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
public class ExperimentTestDataService extends AbstractTestDataLoader<ExperimentTestDataModel> {

    private final AutoTestsProperties autoTestsProperties;

    /**
     * Constructor with spring dependency injection.
     *
     * @param autoTestsProperties - auto tests properties
     */
    public ExperimentTestDataService(AutoTestsProperties autoTestsProperties) {
        super(ExperimentTestDataModel.class);
        this.autoTestsProperties = autoTestsProperties;
    }

    @Override
    protected String getTestDataPath() {
        return autoTestsProperties.getExperimentsDataPath();
    }
}
