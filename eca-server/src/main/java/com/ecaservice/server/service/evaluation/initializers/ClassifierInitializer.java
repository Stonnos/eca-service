package com.ecaservice.server.service.evaluation.initializers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;
import weka.classifiers.AbstractClassifier;
import weka.core.Instances;

/**
 * Abstract class initialization classifiers options based on training data.
 *
 * @param <T> classifier generic type
 * @author Roman Batygin
 */
@Slf4j
public abstract class ClassifierInitializer<T extends AbstractClassifier> {

    private final Class<T> type;

    /**
     * Constructor with classifier type.
     *
     * @param type classifier options type
     */
    protected ClassifierInitializer(Class<T> type) {
        this.type = type;
    }

    /**
     * Returns {@code true} if classifier options can be initialized from data.
     *
     * @param classifier {@link AbstractClassifier} object
     * @return {@code true} if classifier options can be initialized from data
     */
    public boolean canHandle(AbstractClassifier classifier) {
        return classifier != null && type.isAssignableFrom(classifier.getClass());
    }

    /**
     * Initialize classifiers options based on training data.
     *
     * @param data       - training data
     * @param classifier - classifier object
     */
    public void handle(Instances data, T classifier) {
        Assert.notNull(data, "Input data is not specified!");
        Assert.notNull(classifier, "Classifier is not specified!");
        log.info("Starting to initialize classifier [{}] options based on data [{}]",
                classifier.getClass().getSimpleName(), data.relationName());
        internalHandle(data, classifier);
        log.info("Classifier [{}] options has been initialized based on data [{}]",
                classifier.getClass().getSimpleName(), data.relationName());
    }

    protected abstract void internalHandle(Instances data, T classifier);
}
