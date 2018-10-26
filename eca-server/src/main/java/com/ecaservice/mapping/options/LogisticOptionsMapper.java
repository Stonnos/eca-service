package com.ecaservice.mapping.options;

import com.ecaservice.model.options.LogisticOptions;
import eca.regression.Logistic;
import org.mapstruct.Mapper;

/**
 * Implements logistic regression input options mapping to logistic model.
 *
 * @author Roman Batygin
 */
@Mapper
public abstract class LogisticOptionsMapper extends ClassifierOptionsMapper<LogisticOptions, Logistic> {

    protected LogisticOptionsMapper() {
        super(LogisticOptions.class);
    }

}
