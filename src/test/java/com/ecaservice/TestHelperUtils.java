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
import eca.data.file.XLSLoader;
import eca.metrics.KNearestNeighbours;
import weka.core.Instances;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

/**
 * Test data helper class.
 *
 * @author Roman Batygin
 */
public class TestHelperUtils {

    public static final int SEED = 3;
    public static final int NUM_FOLDS = 10;
    public static final int NUM_TESTS = 10;
    public static final String IP_ADDRESS = "127.0.0.1";
    public static final String UUID = "a01ebc99-9c0b-4ef8-bb6d-6bb9bd380a11";

    private static final String FIRST_NAME = "Roman";
    private static final String TEST_MAIL_RU = "test@mail.ru";
    private static final String TRAINING_DATA_ABSOLUTE_PATH = "/home/data";
    private static final String EXPERIMENT_ABSOLUTE_PATH = "/home/experiment";
    private static final String DATA_PATH = "data/iris.xls";
    private static final String SENDER_MAIL = "sender@mail.ru";
    private static final String RECEIVER_MAIL = "receiver@mail.tu";
    private static final String SUBJECT = "subject";
    private static final String MAIL_MESSAGE = "message";

    /**
     * Generates the test data set.
     *
     * @return {@link Instances} object
     */
    public static Instances loadInstances() throws Exception {
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        try (InputStream inputStream = classLoader.getResourceAsStream(DATA_PATH)) {
            XLSLoader xlsLoader = new XLSLoader();
            xlsLoader.setInputStream(inputStream);
            return xlsLoader.getDataSet();
        }
    }

    /**
     * Creates <tt>EvaluationRequest</tt> object
     *
     * @param ipAddress     ip address
     * @return {@link EvaluationRequest} object
     */
    public static EvaluationRequest createEvaluationRequest(String ipAddress) throws Exception {
        EvaluationRequest request = new EvaluationRequest();
        request.setEvaluationMethod(EvaluationMethod.TRAINING_DATA);
        request.setIpAddress(ipAddress);
        request.setEvaluationOptionsMap(Collections.emptyMap());
        request.setInputData(new InputData(new KNearestNeighbours(), loadInstances()));
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
        Map<EvaluationOption, String> evaluationOptionsMap = new EnumMap<>(EvaluationOption.class);
        evaluationOptionsMap.put(EvaluationOption.NUM_FOLDS, String.valueOf(numFolds));
        evaluationOptionsMap.put(EvaluationOption.NUM_TESTS, String.valueOf(numTests));
        return evaluationOptionsMap;
    }

    /**
     * Creates experiment request.
     *
     * @return {@link ExperimentRequest} object
     */
    public static ExperimentRequest createExperimentRequest() throws Exception {
        ExperimentRequest experimentRequest = new ExperimentRequest();
        experimentRequest.setIpAddress(IP_ADDRESS);
        experimentRequest.setExperimentType(ExperimentType.KNN);
        experimentRequest.setEvaluationMethod(EvaluationMethod.TRAINING_DATA);
        experimentRequest.setData(loadInstances());
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
        return createExperiment(uuid, ExperimentStatus.NEW);
    }

    /**
     * Creates experiment.
     *
     * @param uuid             uuid
     * @param experimentStatus experiment status
     * @return {@link Experiment} object
     */
    public static Experiment createExperiment(String uuid, ExperimentStatus experimentStatus) {
        Experiment experiment = new Experiment();
        experiment.setFirstName(FIRST_NAME);
        experiment.setEmail(TEST_MAIL_RU);
        experiment.setExperimentStatus(experimentStatus);
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
    public static InitializationParams createInitializationParams() throws Exception {
        InitializationParams initializationParams = new InitializationParams();
        initializationParams.setData(loadInstances());
        initializationParams.setEvaluationMethod(EvaluationMethod.TRAINING_DATA);
        return initializationParams;
    }

    /**
     * Creates mail.
     * @return {@link Mail} object
     */
    public static Mail createMail() {
        Mail mail = new Mail();
        mail.setSender(SENDER_MAIL);
        mail.setReceiver(RECEIVER_MAIL);
        mail.setSubject(SUBJECT);
        mail.setMessage(MAIL_MESSAGE);
        return mail;
    }

}
