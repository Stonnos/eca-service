package com.ecaservice;

import com.ecaservice.model.InputData;
import com.ecaservice.model.evaluation.EvaluationMethod;
import com.ecaservice.model.evaluation.EvaluationOption;
import com.ecaservice.model.evaluation.EvaluationRequest;
import eca.generators.SimpleDataGenerator;
import eca.metrics.KNearestNeighbours;
import weka.core.Instances;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Test data helper class.
 *
 * @author Roman Batygin
 */
public class TestDataHelper {

    public static final int NUM_INSTANCES = 25;
    public static final int NUM_ATTRIBUTES = 6;
    public static final int SEED = 3;
    public static final int NUM_FOLDS = 10;
    public static final int NUM_TESTS = 10;
    public static final String IP_ADDRESS = "127.0.0.1";

    /**
     * Generates the test data set.
     *
     * @param numInstances  number of instances
     * @param numAttributes number of attributes
     * @return {@link Instances} object
     */
    public static Instances generateInstances(int numInstances, int numAttributes) {
        SimpleDataGenerator simpleDataGenerator = new SimpleDataGenerator();
        simpleDataGenerator.setNumInstances(numInstances);
        simpleDataGenerator.setNumAttributes(numAttributes);
        return simpleDataGenerator.generate();
    }

    /**
     * Creates <tt>EvaluationRequest</tt> object
     *
     * @param ipAddress     ip address
     * @param numInstances  the number of instances
     * @param numAttributes the number of folds
     * @return {@link EvaluationRequest} object
     */
    public static EvaluationRequest createEvaluationRequest(String ipAddress,
                                                            int numInstances,
                                                            int numAttributes) {
        EvaluationRequest request = new EvaluationRequest();
        request.setEvaluationMethod(EvaluationMethod.TRAINING_DATA);
        request.setIpAddress(ipAddress);
        request.setEvaluationOptionsMap(Collections.emptyMap());
        request.setInputData(new InputData(new KNearestNeighbours(), generateInstances(numInstances, numAttributes)));
        return request;
    }

    /**
     * Creates evaluation options map.
     *
     * @param numFolds number of folds
     * @param numTests number of tests
     * @return evaluation options map
     */
    public static Map<EvaluationOption, String> createEvaluationOptionsMap(int numFolds, int numTests) {
        Map<EvaluationOption, String> evaluationOptionsMap = new HashMap<>();
        evaluationOptionsMap.put(EvaluationOption.NUM_FOLDS, String.valueOf(numFolds));
        evaluationOptionsMap.put(EvaluationOption.NUM_TESTS, String.valueOf(numTests));
        return evaluationOptionsMap;
    }

}
