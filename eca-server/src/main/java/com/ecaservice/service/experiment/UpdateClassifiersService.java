package com.ecaservice.service.experiment;

import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

/**
 * Application configuration initializer.
 *
 * @author Roman Batygin
 */
@Service
public class UpdateClassifiersService {

    private final ExperimentConfigurationService experimentConfigurationService;

    /**
     * Constructor with spring dependency injection.
     *
     * @param experimentConfigurationService - experiment configuration service bean
     */
    @Inject
    public UpdateClassifiersService(ExperimentConfigurationService experimentConfigurationService) {
        this.experimentConfigurationService = experimentConfigurationService;
    }

    /**
     * Initialize application configurations.
     */
    @PostConstruct
    public void initialize() {
        experimentConfigurationService.saveClassifiersOptions();
    }
}
