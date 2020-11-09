package com.ecaservice.external.api.service;

import com.ecaservice.external.api.config.ExternalApiConfig;
import eca.converters.ModelConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;

/**
 * Implements data service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DataService {

    private final ExternalApiConfig externalApiConfig;

    /**
     * Saves classifier model to file.
     *
     * @param classifier - classifier
     * @param fileName   - file name
     * @throws Exception in case of error
     */
    public void saveModel(Object classifier, String fileName) throws Exception {
        ModelConverter.saveModel(new File(externalApiConfig.getClassifiersPath(), fileName), classifier);
    }
}
