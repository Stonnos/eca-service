package com.ecaservice.converters;

import com.ecaservice.model.options.ClassifierOptions;
import weka.classifiers.AbstractClassifier;

/**
 * Abstract class for classifier input options converted.
 *
 * @param <S> classifier input options generic type
 * @param <T> classifier generic type
 * @author Roman Batygin
 */
public abstract class ClassifierOptionsConverter<S extends ClassifierOptions, T extends AbstractClassifier> {

    private final Class<S> type;

    /**
     * Constructor with classifier options type.
     *
     * @param type classifier options type
     */
    protected ClassifierOptionsConverter(Class<S> type) {
        this.type = type;
    }

    /**
     * Returns <tt>true</tt> if input options can be convert to specified classifier.
     *
     * @param options {@link ClassifierOptions} object
     * @return <tt>true</tt> if input options can be convert to specified classifier
     */
    public boolean canConvert(ClassifierOptions options) {
        return options != null && type.isAssignableFrom(options.getClass());
    }

    /**
     * Converts classifier input options object to classifier object.
     *
     * @param options options object
     * @return classifier object
     */
    public abstract T convert(S options);
}
