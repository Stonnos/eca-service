package com.ecaservice.server.service.evaluation.initializers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import weka.classifiers.AbstractClassifier;
import weka.core.Instances;

import java.util.List;

/**
 * Classifier initializer service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ClassifierInitializerService {

    private final List<ClassifierInitializer> classifierInitializers;

    /**
     * Initialize classifier based on training data.
     *
     * @param classifier - classifier
     * @param data       - training data
     */
    @SuppressWarnings("unchecked")
    public void initialize(AbstractClassifier classifier, Instances data) {
        classifierInitializers.stream()
                .filter(classifierInitializer -> classifierInitializer.canHandle(classifier))
                .findFirst()
                .ifPresent(classifierInitializer -> classifierInitializer.handle(data, classifier));
    }
}
