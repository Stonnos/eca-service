package com.ecaservice.mapping.options;

import com.ecaservice.mapping.options.AbstractClassifierMapper;
import com.ecaservice.model.options.LogisticOptions;
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
