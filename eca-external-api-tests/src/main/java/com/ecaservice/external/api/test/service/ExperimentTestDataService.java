package com.ecaservice.external.api.test.service;

import com.ecaservice.external.api.test.config.ExternalApiTestsConfig;
import com.ecaservice.external.api.test.model.ExperimentTestDataModel;
import com.ecaservice.test.common.service.AbstractTestDataProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service to read test requests from resources.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
public class ExperimentTestDataService extends AbstractTestDataProvider<ExperimentTestDataModel> {

    private final ExternalApiTestsConfig externalApiTestsConfig;

    /**
     * Constructor with spring dependency injection.
     *
     * @param externalApiTestsConfig - external api tests config
     */
    public ExperimentTestDataService(ExternalApiTestsConfig externalApiTestsConfig) {
        super(ExperimentTestDataModel.class);
        this.externalApiTestsConfig = externalApiTestsConfig;
    }

    @Override
    protected String getTestDataPath() {
        return externalApiTestsConfig.getExperimentTestDataPath();
    }
}
