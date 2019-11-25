package com.ecaservice.service.experiment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * Implements service for updating experiment configs.
 *
 * @author Roman Batygin
 */
@Service
@RequiredArgsConstructor
public class UpdateExperimentConfigsService {

    private final ExperimentConfigurationService experimentConfigurationService;

    /**
     * Updates experiment configs.
     */
    @PostConstruct
    public void updateExperimentConfigs() {
        experimentConfigurationService.saveClassifiersOptions();
    }
}
