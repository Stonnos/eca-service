package com.ecaservice;

import com.ecaservice.model.InputData;
import com.ecaservice.model.Mail;
import com.ecaservice.model.entity.Experiment;
import com.ecaservice.model.evaluation.EvaluationMethod;
import com.ecaservice.model.evaluation.EvaluationOption;
import com.ecaservice.model.evaluation.EvaluationRequest;
import com.ecaservice.model.experiment.ExperimentRequest;
import com.ecaservice.model.experiment.ExperimentStatus;
import com.ecaservice.model.experiment.ExperimentType;
import com.ecaservice.model.experiment.InitializationParams;
import eca.generators.SimpleDataGenerator;
import eca.metrics.KNearestNeighbours;
import weka.core.Instances;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Test data helper class.
 *
 * @author Roman Batygin
 */
public class TestHelperUtils {

    public static final int NUM_INSTANCES = 25;
    public static final int NUM_ATTRIBUTES = 6;
    public static final int SEED = 3;
    public static final int NUM_FOLDS = 10;
    public static final int NUM_TESTS = 10;
    public static final String IP_ADDRESS = "127.0.0.1";
    public static final String FIRST_NAME = "Roman";
    public static final String TEST_MAIL_RU = "test@mail.ru";
    public static final String UUID = "a01ebc99-9c0b-4ef8-bb6d-6bb9bd380a11";
    public static final String TRAINING_DATA_ABSOLUTE_PATH = "/home/data";
    public static final String EXPERIMENT_ABSOLUTE_PATH = "/home/experiment";

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

    /**
     * Creates experiment request.
     *
     * @return {@link ExperimentRequest} object
     */
    public static ExperimentRequest createExperimentRequest() {
        ExperimentRequest experimentRequest = new ExperimentRequest();
        experimentRequest.setIpAddress(IP_ADDRESS);
        experimentRequest.setExperimentType(ExperimentType.KNN);
        experimentRequest.setEvaluationMethod(EvaluationMethod.TRAINING_DATA);
        experimentRequest.setData(generateInstances(NUM_INSTANCES, NUM_TESTS));
        experimentRequest.setFirstName(FIRST_NAME);
        experimentRequest.setEmail(TEST_MAIL_RU);
        return experimentRequest;
    }

    /**
     * Creates experiment.
     *
     * @param uuid uuid
     * @return {@link Experiment} object
     */
    public static Experiment createExperiment(String uuid) {
        Experiment experiment = new Experiment();
        experiment.setFirstName(FIRST_NAME);
        experiment.setEmail(TEST_MAIL_RU);
        experiment.setExperimentStatus(ExperimentStatus.NEW);
        experiment.setCreationDate(LocalDateTime.now());
        experiment.setIpAddress(IP_ADDRESS);
        experiment.setExperimentType(ExperimentType.KNN);
        experiment.setEvaluationMethod(EvaluationMethod.TRAINING_DATA);
        experiment.setTrainingDataAbsolutePath(TRAINING_DATA_ABSOLUTE_PATH);
        experiment.setExperimentAbsolutePath(EXPERIMENT_ABSOLUTE_PATH);
        experiment.setUuid(uuid);
        return experiment;
    }

    /**
     * Creates experiment initialization params.
     *
     * @param data {@link Instances} object
     * @return {@link InitializationParams} object
     */
    public static InitializationParams createInitializationParams(Instances data) {
        InitializationParams initializationParams = new InitializationParams();
        initializationParams.setData(data);
        initializationParams.setEvaluationMethod(EvaluationMethod.TRAINING_DATA);
        return initializationParams;
    }

    /**
     * Creates experiment initialization params.
     *
     * @return {@link InitializationParams} object
     */
    public static InitializationParams createInitializationParams() {
        InitializationParams initializationParams = new InitializationParams();
        initializationParams.setData(generateInstances(NUM_INSTANCES, NUM_ATTRIBUTES));
        initializationParams.setEvaluationMethod(EvaluationMethod.TRAINING_DATA);
        return initializationParams;
    }

    /**
     * Creates mail.
     * @return {@link Mail} object
     */
    public static Mail createMail() {
        Mail mail = new Mail();
        mail.setSender("sender@mail.ru");
        mail.setReceiver("receiver@mail.tu");
        mail.setSubject("subject");
        mail.setMessage("message");
        return mail;
    }

}
