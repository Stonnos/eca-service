package com.ecaservice.service.experiment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

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
     * @param experimentConfigurationService {@link ExperimentConfigurationService} bean
     */
    @Autowired
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
