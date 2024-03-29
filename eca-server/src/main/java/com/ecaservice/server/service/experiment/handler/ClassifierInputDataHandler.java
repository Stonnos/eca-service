package com.ecaservice.server.service.experiment.handler;

import org.springframework.util.Assert;
import weka.classifiers.AbstractClassifier;
import weka.core.Instances;

/**
 * Abstract class initialization classifiers options based on training data.
 *
 * @param <T> classifier generic type
 * @author Roman Batygin
 */
public abstract class ClassifierInputDataHandler<T extends AbstractClassifier> {

    private final Class<T> type;

    /**
     * Constructor with classifier type.
     *
     * @param type classifier options type
     */
    protected ClassifierInputDataHandler(Class<T> type) {
        this.type = type;
    }

    /**
     * Returns <tt>true</tt> if classifier options can be initialized from data.
     *
     * @param classifier {@link AbstractClassifier} object
     * @return <tt>true</tt> if classifier options can be initialized from data
     */
    public boolean canHandle(AbstractClassifier classifier) {
        return classifier != null && type.isAssignableFrom(classifier.getClass());
    }

    /**
     * Initialize classifiers options based on training data.
     *
     * @param data       training data
     * @param classifier classifier object
     */
    public void handle(Instances data, T classifier) {
        Assert.notNull(data, "Input data is not specified!");
        Assert.notNull(classifier, "Classifier is not specified!");
        internalHandle(data, classifier);
    }

    protected abstract void internalHandle(Instances data, T classifier);
}
