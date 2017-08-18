package com.ecaservice;

import com.ecaservice.model.ClassificationResult;
import eca.core.evaluation.Evaluation;
import eca.generators.SimpleDataGenerator;
import eca.metrics.KNearestNeighbours;
import eca.model.ClassifierDescriptor;
import weka.core.Instances;

/**
 * @author Roman Batygin
 */

public class TestDataBuilder {

    public static Instances generate(int numInstances, int numAttributes) {
        SimpleDataGenerator simpleDataGenerator = new SimpleDataGenerator();
        simpleDataGenerator.setNumInstances(numInstances);
        simpleDataGenerator.setNumAttributes(numAttributes);
        return simpleDataGenerator.generate();
    }

    public static ClassificationResult createClassificationResult(int numInstances,
                                                                  int numAttributes) throws Exception {
        ClassificationResult result = new ClassificationResult();
        Instances instances = generate(numInstances, numAttributes);
        KNearestNeighbours kNearestNeighbours = new KNearestNeighbours();
        result.setClassifierDescriptor(new ClassifierDescriptor(kNearestNeighbours,
                new Evaluation(instances)));
        return result;
    }
}
