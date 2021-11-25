package com.ecaservice.server.service.evaluation.initializers;

import eca.trees.DecisionTreeClassifier;
import org.springframework.stereotype.Component;
import weka.core.Instances;

/**
 * Implements decision tree input options initialization
 *
 * @author Roman Batygin
 */
@Component
public class DecisionTreeInitializer extends ClassifierInitializer<DecisionTreeClassifier> {

    protected DecisionTreeInitializer() {
        super(DecisionTreeClassifier.class);
    }

    @Override
    protected void internalHandle(Instances data, DecisionTreeClassifier classifier) {
        if (classifier.isRandomTree() && classifier.getNumRandomAttr() > data.numAttributes() - 1) {
            classifier.setNumRandomAttr(0);
        }
        if (classifier.isUseRandomSplits()) {
            classifier.setUseBinarySplits(true);
        }
    }
}
