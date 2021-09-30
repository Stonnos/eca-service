package com.ecaservice.server.service.experiment.handler;

import eca.trees.DecisionTreeClassifier;
import org.springframework.stereotype.Component;
import weka.core.Instances;

/**
 * Implements decision tree input options initialization. In this implementation
 * the random attributes number is calculated by formula <code>sqrt(k)</code>, where
 * k is input data attributes number.
 *
 * @author Roman Batygin
 */
@Component
public class DecisionTreeInputDataHandler extends ClassifierInputDataHandler<DecisionTreeClassifier> {

    protected DecisionTreeInputDataHandler() {
        super(DecisionTreeClassifier.class);
    }

    @Override
    protected void internalHandle(Instances data, DecisionTreeClassifier classifier) {
        if (classifier.isRandomTree() &&
                (classifier.getNumRandomAttr() == 0 || classifier.getNumRandomAttr() > data.numAttributes() - 1)) {
            classifier.setNumRandomAttr((int) Math.sqrt(data.numAttributes()));
        }
    }
}
