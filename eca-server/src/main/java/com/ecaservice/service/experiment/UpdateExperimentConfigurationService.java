package com.ecaservice.service.experiment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;

/**
 * Service for updating experiment configuration.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UpdateExperimentConfigurationService {

    private final ExperimentConfigurationService experimentConfigurationService;

    /**
     * Updates experiment configuration.
     */
    @PostConstruct
    public void updateExperimentConfiguration() throws IOException {
        experimentConfigurationService.saveClassifiersOptions();
    }
}