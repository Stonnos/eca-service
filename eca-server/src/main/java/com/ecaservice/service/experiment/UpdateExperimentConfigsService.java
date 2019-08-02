package com.ecaservice.service.experiment;

import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

/**
 * Implements service for updating experiment configs.
 *
 * @author Roman Batygin
 */
@Service
public class UpdateExperimentConfigsService {

    private final ExperimentConfigurationService experimentConfigurationService;

    /**
     * Constructor with spring dependency injection.
     *
     * @param experimentConfigurationService - experiment configuration service bean
     */
    @Inject
    public UpdateExperimentConfigsService(ExperimentConfigurationService experimentConfigurationService) {
        this.experimentConfigurationService = experimentConfigurationService;
    }

    /**
     * Updates experiment configs.
     */
    @PostConstruct
    public void updateExperimentConfigs() {
        experimentConfigurationService.saveClassifiersOptions();
    }
}
