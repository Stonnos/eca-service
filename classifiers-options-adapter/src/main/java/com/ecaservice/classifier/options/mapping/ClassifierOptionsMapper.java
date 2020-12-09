package com.ecaservice.classifier.options.mapping;

import com.ecaservice.classifier.options.model.ClassifierOptions;
import weka.classifiers.AbstractClassifier;

/**
 * Abstract class for classifier input options mapping.
 *
 * @param <S> classifier input options generic type
 * @param <T> classifier generic type
 * @author Roman Batygin
 */
public abstract class ClassifierOptionsMapper<S extends ClassifierOptions, T extends AbstractClassifier> {

    private final Class<S> type;

    /**
     * Constructor with classifier options type.
     *
     * @param type classifier options type
     */
    protected ClassifierOptionsMapper(Class<S> type) {
        this.type = type;
    }

    /**
     * Returns <tt>true</tt> if input options can be convert to specified classifier.
     *
     * @param options classifier options
     * @return <tt>true</tt> if input options can be convert to specified classifier
     */
    public boolean canMap(ClassifierOptions options) {
        return options != null && type.isAssignableFrom(options.getClass());
    }

    /**
     * Maps classifier input options object to classifier object.
     *
     * @param source options object
     * @return classifier object
     */
    public abstract T map(S source);
}
