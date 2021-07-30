package com.ecaservice.load.test.service;

import com.ecaservice.classifier.options.model.ClassifierOptions;
import com.ecaservice.load.test.config.EcaLoadTestsConfig;
import com.ecaservice.test.common.service.AbstractTestDataProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Classifiers config service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
public class ClassifiersTestDataProvider extends AbstractTestDataProvider<ClassifierOptions> {

    private final EcaLoadTestsConfig ecaLoadTestsConfig;

    /**
     * Constructor with spring dependency injection.
     *
     * @param ecaLoadTestsConfig - eca load test config
     */
    public ClassifiersTestDataProvider(EcaLoadTestsConfig ecaLoadTestsConfig) {
        super(ClassifierOptions.class);
        this.ecaLoadTestsConfig = ecaLoadTestsConfig;
    }

    @Override
    protected String getTestDataPath() {
        return ecaLoadTestsConfig.getClassifiersStoragePath();
    }
}
