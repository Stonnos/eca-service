package com.ecaservice;

import com.ecaservice.base.model.EvaluationRequest;
import com.ecaservice.base.model.EvaluationResponse;
import com.ecaservice.base.model.ExperimentRequest;
import com.ecaservice.base.model.ExperimentType;
import com.ecaservice.base.model.TechnicalStatus;
import com.ecaservice.classifier.options.model.AdaBoostOptions;
import com.ecaservice.classifier.options.model.BackPropagationOptions;
import com.ecaservice.classifier.options.model.ClassifierOptions;
import com.ecaservice.classifier.options.model.DecisionTreeOptions;
import com.ecaservice.classifier.options.model.ExtraTreesOptions;
import com.ecaservice.classifier.options.model.HeterogeneousClassifierOptions;
import com.ecaservice.classifier.options.model.J48Options;
import com.ecaservice.classifier.options.model.KNearestNeighboursOptions;
import com.ecaservice.classifier.options.model.LogisticOptions;
import com.ecaservice.classifier.options.model.NeuralNetworkOptions;
import com.ecaservice.classifier.options.model.RandomForestsOptions;
import com.ecaservice.classifier.options.model.StackingOptions;
import com.ecaservice.ers.dto.ClassificationCostsReport;
import com.ecaservice.ers.dto.ClassifierOptionsRequest;
import com.ecaservice.ers.dto.ClassifierOptionsResponse;
import com.ecaservice.ers.dto.ClassifierReport;
import com.ecaservice.ers.dto.EvaluationMethodReport;
import com.ecaservice.ers.dto.GetEvaluationResultsResponse;
import com.ecaservice.ers.dto.ResponseStatus;
import com.ecaservice.ers.dto.RocCurveReport;
import com.ecaservice.ers.dto.StatisticsReport;
import com.ecaservice.model.entity.AppInstanceEntity;
import com.ecaservice.model.entity.ClassifierInfo;
import com.ecaservice.model.entity.ClassifierInputOptions;
import com.ecaservice.model.entity.ClassifierOptionsDatabaseModel;
import com.ecaservice.model.entity.ClassifierOptionsRequestEntity;
import com.ecaservice.model.entity.ClassifierOptionsRequestModel;
import com.ecaservice.model.entity.ClassifierOptionsResponseModel;
import com.ecaservice.model.entity.ClassifiersConfiguration;
import com.ecaservice.model.entity.ErsResponseStatus;
import com.ecaservice.model.entity.EvaluationLog;
import com.ecaservice.model.entity.Experiment;
import com.ecaservice.model.entity.ExperimentResultsEntity;
import com.ecaservice.model.entity.ExperimentResultsRequest;
import com.ecaservice.model.entity.InstancesInfo;
import com.ecaservice.model.entity.RequestStatus;
import com.ecaservice.model.evaluation.ClassifierOptionsRequestSource;
import com.ecaservice.model.experiment.ExperimentResultsRequestSource;
import com.ecaservice.model.experiment.InitializationParams;
import com.ecaservice.report.model.BaseReportBean;
import com.ecaservice.web.dto.model.ClassifiersConfigurationDto;
import com.ecaservice.web.dto.model.EnumDto;
import com.ecaservice.web.dto.model.EvaluationResultsDto;
import com.ecaservice.web.dto.model.EvaluationResultsStatus;
import com.ecaservice.web.dto.model.FilterDictionaryDto;
import com.ecaservice.web.dto.model.FilterFieldDto;
import com.ecaservice.web.dto.model.FilterFieldType;
import com.ecaservice.web.dto.model.MatchMode;
import com.ecaservice.web.dto.model.PageRequestDto;
import eca.converters.model.ExperimentHistory;
import eca.core.evaluation.Evaluation;
import eca.core.evaluation.EvaluationMethod;
import eca.core.evaluation.EvaluationResults;
import eca.core.evaluation.EvaluationService;
import eca.data.file.resource.FileResource;
import eca.data.file.xls.XLSLoader;
import eca.ensemble.forests.DecisionTreeType;
import eca.ensemble.sampling.SamplingMethod;
import eca.metrics.KNearestNeighbours;
import eca.trees.CART;
import lombok.experimental.UtilityClass;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.util.DigestUtils;
import weka.core.Instances;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Stream;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newEnumMap;

/**
 * Test data helper class.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class TestHelperUtils {

    public static final int NUM_FOLDS = 3;
    public static final int NUM_TESTS = 1;
    public static final int SEED = 1;
    public static final String TEST_UUID = "a01ebc99-9c0b-4ef8-bb6d-6bb9bd380a11";
    public static final int PAGE_SIZE = 10;
    public static final int PAGE_NUMBER = 0;

    private static final String FIRST_NAME = "Roman";
    private static final String TEST_MAIL_RU = "test@mail.ru";
    private static final String TRAINING_DATA_ABSOLUTE_PATH = "/home/data";
    private static final String EXPERIMENT_ABSOLUTE_PATH = "/home/experiment";
    private static final String DATA_PATH = "data/iris.xls";
    private static final int NUM_OBJ = 2;
    private static final double KNN_WEIGHT = 0.55d;
    private static final int NUM_NEIGHBOURS = 25;
    private static final int NUM_RANDOM_ATTR = 10;
    private static final int NUM_RANDOM_SPLITS = 25;
    private static final String HIDDEN_LAYER = "5,8,9";
    private static final int MAX_DEPTH = 10;
    private static final int NUM_ITERATIONS = 5;
    private static final double MIN_ERROR = 0.00001d;
    private static final int NUM_IN_NEURONS = 10;
    private static final int NUM_OUT_NEURONS = 12;
    private static final double LEARNING_RATE = 0.01d;
    private static final double MOMENTUM = 0.23d;
    private static final double MIN_ERROR_THRESHOLD = 0.0d;
    private static final double MAX_ERROR_THRESHOLD = 0.5d;
    private static final String RELATION_NAME = "Relation";
    private static final String CLASS_NAME = "Class";
    private static final int NUM_INSTANCES = 100;
    private static final int NUM_ATTRIBUTES = 10;
    private static final int NUM_CLASSES = 2;
    private static final String VALUE = "value";
    private static final String BEARER_HEADER_FORMAT = "Bearer %s";
    private static final String FILTER_NAME = "name";
    private static final String FILTER_DESCRIPTION = "description";
    private static final String REPLY_TO = "replyTo";
    private static final String CREATED_BY = "user";
    private static final String CONFIG = "config";
    private static final String OPTION_NAME = "option";
    private static final String OPTION_VALUE = "value";
    private static final String CONFIGURATION_NAME = "configuration";

    /**
     * Creates page request dto.
     *
     * @return page request dto
     */
    public static PageRequestDto createPageRequestDto() {
        return createPageRequestDto(PAGE_NUMBER, PAGE_SIZE);
    }

    /**
     * Creates page request dto.
     *
     * @param page - page number
     * @param size - page size
     * @return page request dto
     */
    public static PageRequestDto createPageRequestDto(Integer page, Integer size) {
        return new PageRequestDto(page, size, null, true, null, newArrayList());
    }

    /**
     * Generates the test data set.
     *
     * @return created training data
     */
    public static Instances loadInstances() {
        try {
            ClassLoader classLoader = ClassLoader.getSystemClassLoader();
            XLSLoader dataLoader = new XLSLoader();
            dataLoader.setSource(new FileResource(new File(classLoader.getResource(DATA_PATH).getFile())));
            return dataLoader.loadInstances();
        } catch (Exception ex) {
            throw new IllegalStateException(ex.getMessage());
        }
    }

    /**
     * Evaluation classifier and returns its evaluation results.
     *
     * @return evaluation results
     */
    public static EvaluationResults getEvaluationResults() {
        try {
            CART cart = new CART();
            Instances testInstances = loadInstances();
            Evaluation evaluation = EvaluationService.evaluateModel(cart, testInstances,
                    eca.core.evaluation.EvaluationMethod.CROSS_VALIDATION, TestHelperUtils.NUM_FOLDS,
                    TestHelperUtils.NUM_TESTS, new Random(SEED));
            return new EvaluationResults(cart, evaluation);
        } catch (Exception ex) {
            throw new IllegalStateException(ex.getMessage());
        }
    }

    /**
     * Creates evaluation request object.
     *
     * @return created evaluation request
     */
    public static EvaluationRequest createEvaluationRequest() {
        EvaluationRequest request = new EvaluationRequest();
        request.setEvaluationMethod(EvaluationMethod.TRAINING_DATA);
        request.setData(loadInstances());
        request.setClassifier(new KNearestNeighbours());
        return request;
    }

    /**
     * Creates experiment request.
     *
     * @return created experiment request
     */
    public static ExperimentRequest createExperimentRequest() {
        ExperimentRequest experimentRequest = new ExperimentRequest();
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
     * @param requestId - request id
     * @return created experiment
     */
    public static Experiment createExperiment(String requestId) {
        return createExperiment(requestId, RequestStatus.NEW);
    }

    /**
     * Creates experiment.
     *
     * @param requestId        - request id
     * @param experimentStatus - experiment status
     * @return created experiment
     */
    public static Experiment createExperiment(String requestId, RequestStatus experimentStatus) {
        Experiment experiment = new Experiment();
        experiment.setFirstName(FIRST_NAME);
        experiment.setEmail(TEST_MAIL_RU);
        experiment.setRequestStatus(experimentStatus);
        experiment.setCreationDate(LocalDateTime.now());
        experiment.setExperimentType(ExperimentType.KNN);
        experiment.setEvaluationMethod(EvaluationMethod.TRAINING_DATA);
        experiment.setTrainingDataAbsolutePath(TRAINING_DATA_ABSOLUTE_PATH);
        experiment.setExperimentAbsolutePath(EXPERIMENT_ABSOLUTE_PATH);
        experiment.setRequestId(requestId);
        experiment.setClassIndex(0);
        return experiment;
    }

    /**
     * Creates experiment.
     *
     * @param requestId         - request id
     * @param experimentStatus  - experiment status
     * @param appInstanceEntity - app instance entity
     * @return created experiment
     */
    public static Experiment createExperiment(String requestId, RequestStatus experimentStatus,
                                              AppInstanceEntity appInstanceEntity) {
        Experiment experiment = createExperiment(requestId, experimentStatus);
        experiment.setAppInstanceEntity(appInstanceEntity);
        return experiment;
    }

    /**
     * Creates sent experiment.
     *
     * @param requestId        - request id
     * @param experimentStatus - experiment status
     * @param sentDate         - sent date
     * @return created experiment
     */
    public static Experiment createSentExperiment(String requestId, RequestStatus experimentStatus,
                                                  LocalDateTime sentDate) {
        Experiment experiment = createExperiment(requestId, experimentStatus);
        experiment.setSentDate(sentDate);
        return experiment;
    }

    /**
     * Creates experiment initialization params.
     *
     * @param data training data
     * @return initialization params
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
     * @return initialization params
     */
    public static InitializationParams createInitializationParams() {
        InitializationParams initializationParams = new InitializationParams();
        initializationParams.setData(loadInstances());
        initializationParams.setEvaluationMethod(EvaluationMethod.TRAINING_DATA);
        return initializationParams;
    }

    /**
     * Creates classifiers options database model.
     *
     * @param config                   - json options configs
     * @param classifiersConfiguration - classifiers configuration entity
     * @return classifier options db model {@see ClassifierOptionsDatabaseModel}
     */
    public static ClassifierOptionsDatabaseModel createClassifierOptionsDatabaseModel(String config,
                                                                                      ClassifiersConfiguration classifiersConfiguration) {
        ClassifierOptionsDatabaseModel classifierOptionsDatabaseModel = new ClassifierOptionsDatabaseModel();
        classifierOptionsDatabaseModel.setOptionsName(OPTION_NAME);
        classifierOptionsDatabaseModel.setConfiguration(classifiersConfiguration);
        classifierOptionsDatabaseModel.setConfig(config);
        classifierOptionsDatabaseModel.setConfigMd5Hash(
                DigestUtils.md5DigestAsHex(config.getBytes(StandardCharsets.UTF_8)));
        classifierOptionsDatabaseModel.setCreationDate(LocalDateTime.now());
        return classifierOptionsDatabaseModel;
    }

    /**
     * Creates classifiers configuration entity.
     *
     * @return classifiers configuration entity
     */
    public static ClassifiersConfiguration createClassifiersConfiguration() {
        ClassifiersConfiguration classifiersConfiguration = new ClassifiersConfiguration();
        classifiersConfiguration.setConfigurationName(CONFIGURATION_NAME);
        classifiersConfiguration.setActive(true);
        classifiersConfiguration.setCreationDate(LocalDateTime.now());
        classifiersConfiguration.setBuildIn(true);
        return classifiersConfiguration;
    }

    /**
     * Creates classifiers configuration dto.
     *
     * @return classifiers configuration dto
     */
    public static ClassifiersConfigurationDto createClassifiersConfigurationDto() {
        ClassifiersConfigurationDto classifiersConfiguration = new ClassifiersConfigurationDto();
        classifiersConfiguration.setActive(true);
        classifiersConfiguration.setCreationDate(LocalDateTime.now());
        classifiersConfiguration.setBuildIn(true);
        return classifiersConfiguration;
    }

    /**
     * Create classifier info.
     *
     * @return classifier info
     */
    public static ClassifierInfo createClassifierInfo() {
        ClassifierInfo classifierInfo = new ClassifierInfo();
        classifierInfo.setClassifierName(CART.class.getSimpleName());
        ClassifierInputOptions classifierInputOptions = new ClassifierInputOptions();
        classifierInputOptions.setOptionName(OPTION_NAME);
        classifierInputOptions.setOptionValue(OPTION_VALUE);
        classifierInputOptions.setOptionOrder(0);
        classifierInfo.setClassifierInputOptions(Collections.singletonList(classifierInputOptions));
        return classifierInfo;
    }

    /**
     * Creates evaluation log.
     *
     * @return evaluation log
     */
    public static EvaluationLog createEvaluationLog() {
        EvaluationLog evaluationLog = new EvaluationLog();
        evaluationLog.setRequestId(UUID.randomUUID().toString());
        evaluationLog.setCreationDate(LocalDateTime.now());
        evaluationLog.setStartDate(LocalDateTime.now());
        evaluationLog.setEndDate(LocalDateTime.now());
        evaluationLog.setClassifierInfo(createClassifierInfo());
        evaluationLog.setInstancesInfo(createInstancesInfo());
        evaluationLog.setEvaluationMethod(EvaluationMethod.CROSS_VALIDATION);
        evaluationLog.setNumFolds(NUM_FOLDS);
        evaluationLog.setNumTests(NUM_TESTS);
        evaluationLog.setSeed(SEED);
        evaluationLog.setRequestStatus(RequestStatus.FINISHED);
        return evaluationLog;
    }

    /**
     * Creates evaluation log with specified request id and status.
     *
     * @param requestId     - request id
     * @param requestStatus - request status
     * @return evaluation log
     */
    public static EvaluationLog createEvaluationLog(String requestId, RequestStatus requestStatus) {
        EvaluationLog evaluationLog = createEvaluationLog();
        evaluationLog.setRequestStatus(requestStatus);
        evaluationLog.setRequestId(requestId);
        return evaluationLog;
    }

    /**
     * Creates instances info.
     *
     * @return instances info
     */
    public static InstancesInfo createInstancesInfo() {
        InstancesInfo instancesInfo = new InstancesInfo();
        instancesInfo.setRelationName(RELATION_NAME);
        instancesInfo.setClassName(CLASS_NAME);
        instancesInfo.setNumInstances(NUM_INSTANCES);
        instancesInfo.setNumAttributes(NUM_ATTRIBUTES);
        instancesInfo.setNumClasses(NUM_CLASSES);
        return instancesInfo;
    }

    /**
     * Creates decision tree options.
     *
     * @return decision tree options
     */
    public static DecisionTreeOptions createDecisionTreeOptions() {
        DecisionTreeOptions decisionTreeOptions = new DecisionTreeOptions();
        decisionTreeOptions.setMaxDepth(MAX_DEPTH);
        decisionTreeOptions.setMinObj(NUM_OBJ);
        decisionTreeOptions.setNumRandomAttr(NUM_RANDOM_ATTR);
        decisionTreeOptions.setNumRandomSplits(NUM_RANDOM_SPLITS);
        decisionTreeOptions.setRandomTree(true);
        decisionTreeOptions.setUseBinarySplits(true);
        decisionTreeOptions.setUseRandomSplits(false);
        decisionTreeOptions.setSeed(SEED);
        return decisionTreeOptions;
    }

    /**
     * Creates neural network options.
     *
     * @return neural network options
     */
    public static NeuralNetworkOptions createNeuralNetworkOptions() {
        NeuralNetworkOptions neuralNetworkOptions = new NeuralNetworkOptions();
        neuralNetworkOptions.setNumIterations(NUM_ITERATIONS);
        neuralNetworkOptions.setSeed(SEED);
        neuralNetworkOptions.setHiddenLayer(HIDDEN_LAYER);
        neuralNetworkOptions.setMinError(MIN_ERROR);
        neuralNetworkOptions.setNumInNeurons(NUM_IN_NEURONS);
        neuralNetworkOptions.setNumOutNeurons(NUM_OUT_NEURONS);
        neuralNetworkOptions.setBackPropagationOptions(new BackPropagationOptions());
        neuralNetworkOptions.getBackPropagationOptions().setMomentum(MOMENTUM);
        neuralNetworkOptions.getBackPropagationOptions().setLearningRate(LEARNING_RATE);
        return neuralNetworkOptions;
    }

    /**
     * Creates J48 options.
     *
     * @return J48 options
     */
    public static J48Options createJ48Options() {
        J48Options j48Options = new J48Options();
        j48Options.setNumFolds(NUM_FOLDS);
        j48Options.setMinNumObj(NUM_OBJ);
        j48Options.setBinarySplits(true);
        j48Options.setUnpruned(false);
        return j48Options;
    }

    /**
     * Creates KNN options.
     *
     * @return KNN options
     */
    public static KNearestNeighboursOptions createKNearestNeighboursOptions() {
        KNearestNeighboursOptions options = new KNearestNeighboursOptions();
        options.setNumNeighbours(NUM_NEIGHBOURS);
        options.setWeight(KNN_WEIGHT);
        return options;
    }

    /**
     * Creates random forests options.
     *
     * @param decisionTreeType - decision tree type
     * @return random forests options
     */
    public static RandomForestsOptions createRandomForestsOptions(DecisionTreeType decisionTreeType) {
        RandomForestsOptions options = new RandomForestsOptions();
        options.setDecisionTreeType(decisionTreeType);
        options.setMinObj(NUM_OBJ);
        options.setMaxDepth(MAX_DEPTH);
        options.setNumRandomAttr(NUM_RANDOM_ATTR);
        options.setNumIterations(NUM_ITERATIONS);
        options.setSeed(SEED);
        return options;
    }

    /**
     * Creates extra trees options.
     *
     * @param decisionTreeType - decision tree type
     * @return extra trees options
     */
    public static ExtraTreesOptions createExtraTreesOptions(DecisionTreeType decisionTreeType) {
        ExtraTreesOptions options = new ExtraTreesOptions();
        options.setDecisionTreeType(decisionTreeType);
        options.setMinObj(NUM_OBJ);
        options.setMaxDepth(MAX_DEPTH);
        options.setNumRandomAttr(NUM_RANDOM_ATTR);
        options.setNumIterations(NUM_ITERATIONS);
        options.setSeed(SEED);
        options.setUseBootstrapSamples(true);
        options.setNumRandomSplits(NUM_RANDOM_SPLITS);
        return options;
    }

    /**
     * Creates stacking options.
     *
     * @return stacking options
     */
    public static StackingOptions createStackingOptions() {
        StackingOptions stackingOptions = new StackingOptions();
        stackingOptions.setSeed(SEED);
        stackingOptions.setNumFolds(NUM_FOLDS);
        stackingOptions.setUseCrossValidation(true);
        stackingOptions.setClassifierOptions(createClassifierOptions());
        stackingOptions.setMetaClassifierOptions(createJ48Options());
        return stackingOptions;
    }

    /**
     * Creates AdaBoost options.
     *
     * @return AdaBoost options
     */
    public static AdaBoostOptions createAdaBoostOptions() {
        AdaBoostOptions options = new AdaBoostOptions();
        options.setNumIterations(NUM_ITERATIONS);
        options.setSeed(SEED);
        options.setMinError(MIN_ERROR_THRESHOLD);
        options.setMaxError(MAX_ERROR_THRESHOLD);
        options.setClassifierOptions(createClassifierOptions());
        return options;
    }

    /**
     * Creates HEC options.
     *
     * @param useRandomSubspaces - is use random subspaces sampling at each iteration?
     * @return HEC options
     */
    public static HeterogeneousClassifierOptions createHeterogeneousClassifierOptions(boolean useRandomSubspaces) {
        HeterogeneousClassifierOptions options = new HeterogeneousClassifierOptions();
        options.setUseRandomSubspaces(useRandomSubspaces);
        options.setSeed(SEED);
        options.setNumIterations(NUM_ITERATIONS);
        options.setUseWeightedVotes(true);
        options.setSamplingMethod(SamplingMethod.BAGGING);
        options.setUseRandomClassifier(true);
        options.setMinError(MIN_ERROR_THRESHOLD);
        options.setMaxError(MAX_ERROR_THRESHOLD);
        options.setClassifierOptions(createClassifierOptions());
        return options;
    }

    /**
     * Creates classifiers options list.
     *
     * @return classifiers options list
     */
    public static List<ClassifierOptions> createClassifierOptions() {
        List<ClassifierOptions> classifierOptions = new ArrayList<>();
        classifierOptions.add(createJ48Options());
        DecisionTreeOptions treeOptions = createDecisionTreeOptions();
        treeOptions.setDecisionTreeType(DecisionTreeType.C45);
        classifierOptions.add(treeOptions);
        return classifierOptions;
    }

    /**
     * Creates classifier report.
     *
     * @return classifier report
     */
    public static ClassifierReport createClassifierReport() {
        return createClassifierReport(Arrays.asList(new CART().getOptions()).toString());
    }

    /**
     * Creates classifier report with specified options.
     *
     * @param options - classifier options
     * @return classifier report
     */
    public static ClassifierReport createClassifierReport(String options) {
        ClassifierReport classifierReport = new ClassifierReport();
        classifierReport.setClassifierName(DecisionTreeType.CART.name());
        classifierReport.setClassifierDescription(DecisionTreeType.CART.getDescription());
        classifierReport.setClassifierInputOptions(newArrayList());
        classifierReport.setOptions(options);
        return classifierReport;
    }

    /**
     * Creates evaluation method report.
     *
     * @return evaluation method
     */
    public static EvaluationMethodReport createEvaluationMethodReport() {
        EvaluationMethodReport evaluationMethodReport = new EvaluationMethodReport();
        evaluationMethodReport.setEvaluationMethod(com.ecaservice.ers.dto.EvaluationMethod.CROSS_VALIDATION);
        evaluationMethodReport.setNumFolds(BigInteger.valueOf(NUM_FOLDS));
        evaluationMethodReport.setNumTests(BigInteger.valueOf(NUM_TESTS));
        evaluationMethodReport.setSeed(BigInteger.valueOf(SEED));
        return evaluationMethodReport;
    }

    /**
     * Creates classifier options request.
     *
     * @return classifier options request
     */
    public static ClassifierOptionsRequest createClassifierOptionsRequest() {
        ClassifierOptionsRequest classifierOptionsRequest = new ClassifierOptionsRequest();
        classifierOptionsRequest.setEvaluationMethodReport(new EvaluationMethodReport());
        classifierOptionsRequest.setEvaluationMethodReport(createEvaluationMethodReport());
        return classifierOptionsRequest;
    }

    /**
     * Creates classifier options response.
     *
     * @param classifierReports - classifier reports
     * @param responseStatus    - response status
     * @return classifier options response
     */
    public static ClassifierOptionsResponse createClassifierOptionsResponse(List<ClassifierReport> classifierReports,
                                                                            ResponseStatus responseStatus) {
        ClassifierOptionsResponse response = new ClassifierOptionsResponse();
        response.setStatus(responseStatus);
        response.setRequestId(java.util.UUID.randomUUID().toString());
        response.setClassifierReports(newArrayList());
        classifierReports.forEach(classifierReport -> response.getClassifierReports().add(classifierReport));
        return response;
    }

    /**
     * Creates classifier options request model.
     *
     * @param dataMd5Hash                     - data MD5 hash
     * @param requestDate                     - request date
     * @param responseStatus                  - response status
     * @param classifierOptionsResponseModels - classifier options response models list
     * @return classifier options request model
     */
    public static ClassifierOptionsRequestModel createClassifierOptionsRequestModel(String dataMd5Hash,
                                                                                    LocalDateTime requestDate,
                                                                                    ErsResponseStatus responseStatus,
                                                                                    List<ClassifierOptionsResponseModel> classifierOptionsResponseModels) {
        ClassifierOptionsRequestModel requestModel = new ClassifierOptionsRequestModel();
        requestModel.setRequestId(UUID.randomUUID().toString());
        requestModel.setRelationName(RELATION_NAME);
        requestModel.setDataMd5Hash(dataMd5Hash);
        requestModel.setRequestDate(requestDate);
        requestModel.setResponseStatus(responseStatus);
        requestModel.setEvaluationMethod(EvaluationMethod.CROSS_VALIDATION);
        requestModel.setNumFolds(NUM_FOLDS);
        requestModel.setNumTests(NUM_TESTS);
        requestModel.setSeed(SEED);
        requestModel.setClassifierOptionsResponseModels(classifierOptionsResponseModels);
        return requestModel;
    }

    /**
     * Creates classifier options request entity.
     *
     * @param creationDate - creation date
     * @param requestModel - classifier options request model
     * @return classifier options request entity
     */
    public static ClassifierOptionsRequestEntity createClassifierOptionsRequestEntity(LocalDateTime creationDate,
                                                                                      ClassifierOptionsRequestModel requestModel) {
        ClassifierOptionsRequestEntity requestEntity = new ClassifierOptionsRequestEntity();
        requestEntity.setCreationDate(creationDate);
        requestEntity.setClassifierOptionsRequestModel(requestModel);
        requestEntity.setSource(ClassifierOptionsRequestSource.ERS);
        return requestEntity;
    }

    /**
     * Creates classifier options response model.
     *
     * @param options - classifier options
     * @return classifier options response model
     */
    public static ClassifierOptionsResponseModel createClassifierOptionsResponseModel(String options) {
        ClassifierOptionsResponseModel responseModel = new ClassifierOptionsResponseModel();
        responseModel.setClassifierName(DecisionTreeType.CART.name());
        responseModel.setClassifierDescription(DecisionTreeType.CART.getDescription());
        responseModel.setOptions(options);
        return responseModel;
    }

    /**
     * Creates experiment results entity.
     *
     * @param experiment - experiment entity
     * @return experiment results entity
     */
    public static ExperimentResultsEntity createExperimentResultsEntity(Experiment experiment) {
        ExperimentResultsEntity experimentResultsEntity = new ExperimentResultsEntity();
        experimentResultsEntity.setExperiment(experiment);
        experimentResultsEntity.setResultsIndex(0);
        experimentResultsEntity.setClassifierInfo(createClassifierInfo());
        return experimentResultsEntity;
    }

    /**
     * Creates experiment results request.
     *
     * @param experimentResultsEntity - experiment results entity
     * @param responseStatus          - response status
     * @return experiment results request
     */
    public static ExperimentResultsRequest createExperimentResultsRequest(
            ExperimentResultsEntity experimentResultsEntity, ErsResponseStatus responseStatus) {
        ExperimentResultsRequest experimentResultsRequest = new ExperimentResultsRequest();
        experimentResultsRequest.setExperimentResults(experimentResultsEntity);
        experimentResultsRequest.setResponseStatus(responseStatus);
        experimentResultsRequest.setRequestDate(LocalDateTime.now());
        experimentResultsRequest.setRequestId(UUID.randomUUID().toString());
        experimentResultsRequest.setRequestSource(ExperimentResultsRequestSource.SYSTEM);
        return experimentResultsRequest;
    }

    /**
     * Creates filter field dto.
     *
     * @return filter field dto
     */
    public static FilterFieldDto createFilterFieldDto() {
        FilterFieldDto filterField = new FilterFieldDto();
        filterField.setDescription(FILTER_DESCRIPTION);
        filterField.setFieldOrder(1);
        filterField.setFieldName(FILTER_NAME);
        filterField.setFilterFieldType(FilterFieldType.REFERENCE);
        filterField.setMatchMode(MatchMode.EQUALS);
        filterField.setMultiple(false);
        return filterField;
    }

    /**
     * Creates filter dictionary dto.
     *
     * @return filter dictionary dto
     */
    public static FilterDictionaryDto createFilterDictionaryDto() {
        FilterDictionaryDto filterDictionaryDto = new FilterDictionaryDto();
        filterDictionaryDto.setName(FILTER_NAME);
        filterDictionaryDto.setValues(Collections.emptyList());
        return filterDictionaryDto;
    }

    /**
     * Creates statistics report.
     *
     * @return statistics report
     */
    public static StatisticsReport createStatisticsReport() {
        StatisticsReport statisticsReport = new StatisticsReport();
        statisticsReport.setNumCorrect(BigInteger.TEN);
        statisticsReport.setNumIncorrect(BigInteger.ONE);
        statisticsReport.setNumTestInstances(BigInteger.TEN);
        statisticsReport.setMaxAucValue(BigDecimal.ONE);
        statisticsReport.setVarianceError(BigDecimal.ZERO);
        statisticsReport.setPctCorrect(BigDecimal.ONE);
        statisticsReport.setPctIncorrect(BigDecimal.TEN);
        statisticsReport.setMeanAbsoluteError(BigDecimal.ONE);
        statisticsReport.setRootMeanSquaredError(BigDecimal.TEN);
        statisticsReport.setConfidenceIntervalLowerBound(BigDecimal.ZERO);
        statisticsReport.setConfidenceIntervalUpperBound(BigDecimal.ONE);
        return statisticsReport;
    }

    /**
     * Creates roc - curve report.
     *
     * @return roc - curve report
     */
    public static RocCurveReport createRocCurveReport() {
        RocCurveReport rocCurveReport = new RocCurveReport();
        rocCurveReport.setAucValue(BigDecimal.ONE);
        rocCurveReport.setSensitivity(BigDecimal.TEN);
        rocCurveReport.setSpecificity(BigDecimal.ZERO);
        rocCurveReport.setThresholdValue(BigDecimal.ONE);
        return rocCurveReport;
    }

    /**
     * Creates classification costs report.
     *
     * @return classification costs report
     */
    public static ClassificationCostsReport createClassificationCostsReport() {
        ClassificationCostsReport classificationCostsReport = new ClassificationCostsReport();
        classificationCostsReport.setClassValue(VALUE);
        classificationCostsReport.setFalseNegativeRate(BigDecimal.ONE);
        classificationCostsReport.setFalsePositiveRate(BigDecimal.ZERO);
        classificationCostsReport.setTrueNegativeRate(BigDecimal.ONE);
        classificationCostsReport.setTruePositiveRate(BigDecimal.ZERO);
        classificationCostsReport.setRocCurve(createRocCurveReport());
        return classificationCostsReport;
    }

    /**
     * Creates get evaluation results response.
     *
     * @param requestId      - request id
     * @param responseStatus - response status
     * @return get evaluation results response
     */
    public static GetEvaluationResultsResponse createGetEvaluationResultsResponse(String requestId,
                                                                                  ResponseStatus responseStatus) {
        GetEvaluationResultsResponse evaluationResultsResponse = new GetEvaluationResultsResponse();
        evaluationResultsResponse.setRequestId(requestId);
        evaluationResultsResponse.setStatus(responseStatus);
        return evaluationResultsResponse;
    }

    /**
     * Creates evaluation results dto with specified status.
     *
     * @param evaluationResultsStatus - evaluation results status
     * @return evaluation results dto
     */
    public static EvaluationResultsDto createEvaluationResultsDto(EvaluationResultsStatus evaluationResultsStatus) {
        EvaluationResultsDto evaluationResultsDto = new EvaluationResultsDto();
        evaluationResultsDto.setEvaluationResultsStatus(
                new EnumDto(evaluationResultsStatus.name(), evaluationResultsStatus.getDescription()));
        return evaluationResultsDto;
    }

    /**
     * Creates evaluation response with specified request id.
     *
     * @param requestId - request id
     * @return evaluation response
     */
    public static EvaluationResponse createEvaluationResponse(String requestId) {
        EvaluationResponse evaluationResponse = new EvaluationResponse();
        evaluationResponse.setRequestId(requestId);
        evaluationResponse.setStatus(TechnicalStatus.SUCCESS);
        return evaluationResponse;
    }

    /**
     * Creates experiment history.
     *
     * @return experiment history
     */
    public static ExperimentHistory createExperimentHistory() {
        ExperimentHistory experimentHistory = new ExperimentHistory();
        experimentHistory.setExperiment(newArrayList());
        experimentHistory.getExperiment().add(getEvaluationResults());
        return experimentHistory;
    }

    /**
     * Creates authorization header with bearer token.
     *
     * @param token - token
     * @return authorization header with bearer token
     */
    public static String bearerHeader(String token) {
        return String.format(BEARER_HEADER_FORMAT, token);
    }

    /**
     * Creates request status statistics map.
     *
     * @return request status statistics map
     */
    public static Map<RequestStatus, Long> buildRequestStatusStatisticsMap() {
        RequestStatus[] requestStatuses = RequestStatus.values();
        Map<RequestStatus, Long> requestStatusMap = newEnumMap(RequestStatus.class);
        Stream.of(requestStatuses).forEach(
                requestStatus -> requestStatusMap.put(requestStatus, (long) requestStatus.ordinal()));
        return requestStatusMap;
    }

    /**
     * Creates experiment types statistics map.
     *
     * @return experiment types statistics map
     */
    public static Map<ExperimentType, Long> buildExperimentTypeStatisticMap() {
        ExperimentType[] experimentTypes = ExperimentType.values();
        Map<ExperimentType, Long> experimentTypesMap = newEnumMap(ExperimentType.class);
        Stream.of(experimentTypes).forEach(
                experimentType -> experimentTypesMap.put(experimentType, (long) experimentType.ordinal()));
        return experimentTypesMap;
    }

    /**
     * Creates message properties.
     *
     * @return message properties
     */
    public static MessageProperties buildMessageProperties() {
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setReplyTo(REPLY_TO);
        messageProperties.setCorrelationId(UUID.randomUUID().toString());
        return messageProperties;
    }

    /**
     * Creates logistic options.
     *
     * @return logistic options
     */
    public LogisticOptions createLogisticOptions() {
        LogisticOptions logisticOptions = new LogisticOptions();
        logisticOptions.setMaxIts(NUM_ITERATIONS);
        logisticOptions.setUseConjugateGradientDescent(false);
        return logisticOptions;
    }

    /**
     * Creates report bean.
     *
     * @param <T> - item generic type
     * @return base report bean
     */
    public static <T> BaseReportBean<T> createReportBean() {
        return BaseReportBean.<T>builder()
                .items(Collections.emptyList())
                .filters(Collections.emptyList())
                .build();
    }

    /**
     * Creates classifier options database model.
     *
     * @return classifier options database model
     */
    public static ClassifierOptionsDatabaseModel createClassifierOptionsDatabaseModel() {
        ClassifierOptionsDatabaseModel classifierOptionsDatabaseModel = new ClassifierOptionsDatabaseModel();
        classifierOptionsDatabaseModel.setConfig(CONFIG);
        classifierOptionsDatabaseModel.setCreationDate(LocalDateTime.now());
        classifierOptionsDatabaseModel.setOptionsName(DecisionTreeOptions.class.getSimpleName());
        classifierOptionsDatabaseModel.setCreatedBy(CREATED_BY);
        return classifierOptionsDatabaseModel;
    }
}
