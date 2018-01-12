package com.ecaservice.config;

import com.ecaservice.service.experiment.ExperimentConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * Application configuration initializer.
 *
 * @author Roman Batygin
 */
@Service
public class AppInitializer {

    private final ExperimentConfigurationService experimentConfigurationService;

    /**
     * Constructor with spring dependency injection.
     *
     * @param experimentConfigurationService {@link ExperimentConfigurationService} bean
     */
    @Autowired
    public AppInitializer(ExperimentConfigurationService experimentConfigurationService) {
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
