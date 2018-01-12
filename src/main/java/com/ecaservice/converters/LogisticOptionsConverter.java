package com.ecaservice.converters;

import com.ecaservice.model.options.LogisticOptions;
import eca.regression.Logistic;
import org.springframework.stereotype.Component;

/**
 * Implements logistic regression options converted.
 *
 * @author Roman Batygin
 */
@Component
public class LogisticOptionsConverter extends ClassifierOptionsConverter<LogisticOptions, Logistic> {

    /**
     * Default constructor.
     */
    public LogisticOptionsConverter() {
        super(LogisticOptions.class);
    }

    @Override
    public Logistic convert(LogisticOptions logisticOptions) {
        Logistic logistic = new Logistic();
        //logistic.setMaxIts(logisticOptions.getNumIts());
        logistic.setUseConjugateGradientDescent(logisticOptions.getUseConjugateGradientDescent());
        return logistic;
    }
}
