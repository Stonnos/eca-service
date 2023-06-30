package com.ecaservice.classifier.options.mapping;

import com.ecaservice.classifier.options.model.LogisticOptions;
import eca.regression.Logistic;
import org.mapstruct.Mapper;
import org.springframework.core.annotation.Order;

/**
 * Implements logistic regression input options mapping to logistic model.
 *
 * @author Roman Batygin
 */
@Order(Ordered.LOGISTIC_ORDER)
@Mapper
public abstract class LogisticOptionsMapper extends ClassifierOptionsMapper<LogisticOptions, Logistic> {

    protected LogisticOptionsMapper() {
        super(LogisticOptions.class);
    }

}
