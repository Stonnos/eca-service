package com.ecaservice.classifier.options.mapping;

import com.ecaservice.classifier.options.model.LogisticOptions;
import eca.regression.Logistic;
import org.mapstruct.Mapper;
import org.springframework.core.annotation.Order;

/**
 * Implements mapping Logistic regression classifier to its options model.
 *
 * @author Roman Batygin
 */
@Order(Ordered.LOGISTIC_ORDER)
@Mapper
public abstract class LogisticMapper extends AbstractClassifierMapper<Logistic, LogisticOptions> {

    protected LogisticMapper() {
        super(Logistic.class);
    }
}
