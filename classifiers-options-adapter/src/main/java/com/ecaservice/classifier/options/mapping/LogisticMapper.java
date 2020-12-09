package com.ecaservice.classifier.options.mapping;

import com.ecaservice.classifier.options.model.LogisticOptions;
import eca.regression.Logistic;
import org.mapstruct.Mapper;

/**
 * Implements mapping Logistic regression classifier to its options model.
 *
 * @author Roman Batygin
 */
@Mapper
public abstract class LogisticMapper extends AbstractClassifierMapper<Logistic, LogisticOptions> {

    protected LogisticMapper() {
        super(Logistic.class);
    }
}
