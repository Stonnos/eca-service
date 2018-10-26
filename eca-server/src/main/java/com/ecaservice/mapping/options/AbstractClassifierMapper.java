package com.ecaservice.mapping.options;

import com.ecaservice.model.options.ClassifierOptions;
import weka.classifiers.AbstractClassifier;

/**
 * @author Roman Batygin
 */
public abstract class AbstractClassifierMapper <S extends AbstractClassifier, T extends ClassifierOptions> {

    private final Class<S> type;

    /**
     * Constructor with classifier options type.
     *
     * @param type classifier options type
     */
    protected AbstractClassifierMapper(Class<S> type) {
        this.type = type;
    }

    /**
     * Returns <tt>true</tt> if classifier can be convert to specified classifier options.
     *
     * @param classifier classifier
     * @return <tt>true</tt> if classifier can be convert to specified classifier options
     */
    public boolean canMap(AbstractClassifier classifier) {
        return classifier != null && type.isAssignableFrom(classifier.getClass());
    }

    /**
     * Maps classifier object to classifier input options.
     *
     * @param source classifier object
     * @return classifier input options object
     */
    public abstract T map(S source);
}
