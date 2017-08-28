package com.ecaservice;

import com.ecaservice.dto.ClassificationResult;
import eca.core.evaluation.Evaluation;
import eca.generators.SimpleDataGenerator;
import eca.metrics.KNearestNeighbours;
import eca.model.ClassifierDescriptor;
import weka.core.Instances;

/**
 * Test data builder class.
 *
 * @author Roman Batygin
 */
public class TestDataBuilder {

    /**
     * Generates the test data set.
     *
     * @param numInstances  number of instances
     * @param numAttributes number of attributes
     * @return {@link Instances} object
     */
    public static Instances generate(int numInstances, int numAttributes) {
        SimpleDataGenerator simpleDataGenerator = new SimpleDataGenerator();
        simpleDataGenerator.setNumInstances(numInstances);
        simpleDataGenerator.setNumAttributes(numAttributes);
        return simpleDataGenerator.generate();
    }

    /**
     * Creates classification results.
     *
     * @param numInstances  number of instances
     * @param numAttributes number of attributes
     * @return {@link ClassificationResult} object
     * @throws Exception
     */
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
