package com.ecaservice.external.api.service;

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

    /**
     * Saves classifier model to file.
     *
     * @param classifier - classifier
     * @param file       - file object
     * @throws Exception in case of error
     */
    public void saveModel(Object classifier, File file) throws Exception {
        ModelConverter.saveModel(file, classifier);
    }
}
