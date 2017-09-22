package com.ecaservice;

import com.ecaservice.model.EvaluationRequest;
import com.ecaservice.model.entity.EvaluationMethod;
import eca.generators.SimpleDataGenerator;
import eca.metrics.KNearestNeighbours;
import com.ecaservice.model.InputData;
import weka.core.Instances;

import java.util.Date;

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
        request.setRequestDate(new Date());
        request.setIpAddress(ipAddress);
        request.setInputData(new InputData(new KNearestNeighbours(),
                generate(numInstances, numAttributes)));
        return request;
    }

}
