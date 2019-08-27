package com.ecaservice;

import com.ecaservice.dto.EvaluationRequest;
import com.ecaservice.dto.ExperimentRequest;
import com.ecaservice.dto.evaluation.ClassificationCostsReport;
import com.ecaservice.dto.evaluation.ClassifierOptionsRequest;
import com.ecaservice.dto.evaluation.ClassifierOptionsResponse;
import com.ecaservice.dto.evaluation.ClassifierReport;
import com.ecaservice.dto.evaluation.EvaluationMethodReport;
import com.ecaservice.dto.evaluation.GetEvaluationResultsResponse;
import com.ecaservice.dto.evaluation.InputOptionsMap;
import com.ecaservice.dto.evaluation.ResponseStatus;
import com.ecaservice.dto.evaluation.RocCurveReport;
import com.ecaservice.dto.evaluation.StatisticsReport;
import com.ecaservice.model.entity.ClassifierOptionsDatabaseModel;
import com.ecaservice.model.entity.ClassifierOptionsRequestEntity;
import com.ecaservice.model.entity.ClassifierOptionsRequestModel;
import com.ecaservice.model.entity.ClassifierOptionsResponseModel;
import com.ecaservice.model.entity.ErsResponseStatus;
import com.ecaservice.model.entity.EvaluationLog;
import com.ecaservice.model.entity.Experiment;
import com.ecaservice.model.entity.ExperimentResultsEntity;
import com.ecaservice.model.entity.ExperimentResultsRequest;
import com.ecaservice.model.entity.FilterDictionaryValue;
import com.ecaservice.model.entity.InstancesInfo;
import com.ecaservice.model.entity.RequestStatus;
import com.ecaservice.model.evaluation.ClassifierOptionsRequestSource;
import com.ecaservice.model.evaluation.EvaluationOption;
import com.ecaservice.model.experiment.ExperimentResultsRequestSource;
import com.ecaservice.model.experiment.ExperimentType;
import com.ecaservice.model.experiment.InitializationParams;
import com.ecaservice.model.options.AdaBoostOptions;
import com.ecaservice.model.options.BackPropagationOptions;
import com.ecaservice.model.options.ClassifierOptions;
import com.ecaservice.model.options.DecisionTreeOptions;
import com.ecaservice.model.options.ExtraTreesOptions;
import com.ecaservice.model.options.HeterogeneousClassifierOptions;
import com.ecaservice.model.options.J48Options;
import com.ecaservice.model.options.KNearestNeighboursOptions;
import com.ecaservice.model.options.NeuralNetworkOptions;
import com.ecaservice.model.options.RandomForestsOptions;
import com.ecaservice.model.options.RandomNetworkOptions;
import com.ecaservice.model.options.StackingOptions;
import eca.core.evaluation.Evaluation;
import eca.core.evaluation.EvaluationMethod;
import eca.core.evaluation.EvaluationResults;
import eca.core.evaluation.EvaluationService;
import eca.data.file.resource.FileResource;
import eca.data.file.xls.XLSLoader;
import eca.ensemble.RandomNetworks;
import eca.ensemble.StackingClassifier;
import eca.ensemble.forests.DecisionTreeBuilder;
import eca.ensemble.forests.DecisionTreeType;
import eca.ensemble.forests.ExtraTreesClassifier;
import eca.ensemble.forests.RandomForests;
import eca.ensemble.sampling.SamplingMethod;
import eca.metrics.KNearestNeighbours;
import eca.metrics.distances.Distance;
import eca.neural.NeuralNetwork;
import eca.neural.functions.AbstractFunction;
import eca.trees.CART;
import eca.trees.DecisionTreeClassifier;
import eca.trees.J48;
import weka.core.Instances;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

/**
 * Test data helper class.
 *
 * @author Roman Batygin
 */
public class TestHelperUtils {

    public static final int NUM_FOLDS = 3;
    public static final int NUM_TESTS = 1;
    public static final int SEED = 1;
    public static final String TEST_UUID = "a01ebc99-9c0b-4ef8-bb6d-6bb9bd380a11";

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
    private static final int IN_LAYER_NEURONS_NUM = 12;
    private static final int OUT_LAYER_NEURONS_NUM = 7;
    private static final int MAX_DEPTH = 10;
    private static final int NUM_ITERATIONS = 5;
    private static final int NUM_THREADS = 2;
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
    private static final String LABEL = "label";
    private static final String VALUE = "value";

    /**
     * Generates the test data set.
     *
     * @return created training data
     */
    public static Instances loadInstances() throws Exception {
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        XLSLoader dataLoader = new XLSLoader();
        dataLoader.setSource(new FileResource(new File(classLoader.getResource(DATA_PATH).getFile())));
        return dataLoader.loadInstances();
    }

    /**
     * Evaluation classifier and returns its evaluation results.
     *
     * @return evaluation results
     * @throws Exception in case of error
     */
    public static EvaluationResults getEvaluationResults() throws Exception {
        CART cart = new CART();
        Instances testInstances = loadInstances();
        Evaluation evaluation = EvaluationService.evaluateModel(cart, testInstances,
                eca.core.evaluation.EvaluationMethod.CROSS_VALIDATION, TestHelperUtils.NUM_FOLDS,
                TestHelperUtils.NUM_TESTS, new Random(SEED));
        return new EvaluationResults(cart, evaluation);
    }

    /**
     * Creates evaluation request object.
     *
     * @return created evaluation request
     */
    public static EvaluationRequest createEvaluationRequest() throws Exception {
        EvaluationRequest request = new EvaluationRequest();
        request.setEvaluationMethod(EvaluationMethod.TRAINING_DATA);
        request.setEvaluationOptionsMap(Collections.emptyMap());
        request.setData(loadInstances());
        request.setClassifier(new KNearestNeighbours());
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
     * @return created experiment request
     */
    public static ExperimentRequest createExperimentRequest() throws Exception {
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
     * @param uuid uuid
     * @return created experiment
     */
    public static Experiment createExperiment(String uuid) {
        return createExperiment(uuid, RequestStatus.NEW);
    }

    /**
     * Creates experiment.
     *
     * @param uuid             uuid
     * @param experimentStatus experiment status
     * @return created experiment
     */
    public static Experiment createExperiment(String uuid, RequestStatus experimentStatus) {
        Experiment experiment = new Experiment();
        experiment.setFirstName(FIRST_NAME);
        experiment.setEmail(TEST_MAIL_RU);
        experiment.setExperimentStatus(experimentStatus);
        experiment.setCreationDate(LocalDateTime.now());
        experiment.setExperimentType(ExperimentType.KNN);
        experiment.setEvaluationMethod(EvaluationMethod.TRAINING_DATA);
        experiment.setTrainingDataAbsolutePath(TRAINING_DATA_ABSOLUTE_PATH);
        experiment.setExperimentAbsolutePath(EXPERIMENT_ABSOLUTE_PATH);
        experiment.setUuid(uuid);
        return experiment;
    }

    /**
     * Creates sent experiment.
     *
     * @param uuid             uuid
     * @param experimentStatus experiment status
     * @param sentDate         sent date
     * @return created experiment
     */
    public static Experiment createSentExperiment(String uuid, RequestStatus experimentStatus,
                                                  LocalDateTime sentDate) {
        Experiment experiment = createExperiment(uuid, experimentStatus);
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
    public static InitializationParams createInitializationParams() throws Exception {
        InitializationParams initializationParams = new InitializationParams();
        initializationParams.setData(loadInstances());
        initializationParams.setEvaluationMethod(EvaluationMethod.TRAINING_DATA);
        return initializationParams;
    }

    /**
     * Creates classifiers options database model.
     *
     * @param config  json options configs
     * @param version configs version
     * @return classifier options db model {@see ClassifierOptionsDatabaseModel}
     */
    public static ClassifierOptionsDatabaseModel createClassifierOptionsDatabaseModel(String config, int version) {
        ClassifierOptionsDatabaseModel classifierOptionsDatabaseModel = new ClassifierOptionsDatabaseModel();
        classifierOptionsDatabaseModel.setVersion(version);
        classifierOptionsDatabaseModel.setConfig(config);
        classifierOptionsDatabaseModel.setCreationDate(LocalDateTime.now());
        return classifierOptionsDatabaseModel;
    }

    /**
     * Creates evaluation log.
     *
     * @return evaluation log
     */
    public static EvaluationLog createEvaluationLog() {
        EvaluationLog evaluationLog = new EvaluationLog();
        evaluationLog.setCreationDate(LocalDateTime.now());
        evaluationLog.setStartDate(LocalDateTime.now());
        evaluationLog.setEndDate(LocalDateTime.now());
        evaluationLog.setClassifierName(CART.class.getSimpleName());
        evaluationLog.setEvaluationMethod(EvaluationMethod.CROSS_VALIDATION);
        evaluationLog.setEvaluationStatus(RequestStatus.FINISHED);
        evaluationLog.setEvaluationOptionsMap(Collections.emptyMap());
        return evaluationLog;
    }

    /**
     * Creates evaluation log with specified uuid and status.
     *
     * @param uuid          - request id
     * @param requestStatus - request status
     * @return evaluation log
     */
    public static EvaluationLog createEvaluationLog(String uuid, RequestStatus requestStatus) {
        EvaluationLog evaluationLog = createEvaluationLog();
        evaluationLog.setEvaluationStatus(requestStatus);
        evaluationLog.setRequestId(uuid);
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
     * Creates J48 classifier.
     *
     * @return J48 classifier
     */
    public static J48 createJ48() {
        J48 j48 = new J48();
        j48.setUnpruned(true);
        j48.setNumFolds(NUM_FOLDS);
        j48.setMinNumObj(NUM_OBJ);
        j48.setBinarySplits(true);
        return j48;
    }

    /**
     * Creates KNN with specified distance function.
     *
     * @param distance - distance function
     * @return KNN object
     */
    public static KNearestNeighbours createKNearestNeighbours(Distance distance) {
        KNearestNeighbours kNearestNeighbours = new KNearestNeighbours();
        kNearestNeighbours.setDistance(distance);
        kNearestNeighbours.setWeight(KNN_WEIGHT);
        kNearestNeighbours.setNumNeighbours(NUM_NEIGHBOURS);
        return kNearestNeighbours;
    }

    /**
     * Creates decision tree.
     *
     * @param treeType - tree type.
     * @return decision tree object
     */
    public static DecisionTreeClassifier createDecisionTreeClassifier(DecisionTreeType treeType) {
        DecisionTreeClassifier treeClassifier = treeType.handle(new DecisionTreeBuilder());
        treeClassifier.setSeed(SEED);
        treeClassifier.setMinObj(NUM_OBJ);
        treeClassifier.setUseBinarySplits(true);
        treeClassifier.setRandomTree(true);
        treeClassifier.setNumRandomAttr(NUM_RANDOM_ATTR);
        treeClassifier.setNumRandomSplits(NUM_RANDOM_SPLITS);
        return treeClassifier;
    }

    /**
     * Creates neural network with specified activation function.
     *
     * @param abstractFunction - activation function
     * @return neural network object
     */
    public static NeuralNetwork createNeuralNetwork(AbstractFunction abstractFunction) {
        NeuralNetwork neuralNetwork = new NeuralNetwork();
        neuralNetwork.setSeed(SEED);
        neuralNetwork.getMultilayerPerceptron().setNumInNeurons(IN_LAYER_NEURONS_NUM);
        neuralNetwork.getMultilayerPerceptron().setNumOutNeurons(OUT_LAYER_NEURONS_NUM);
        neuralNetwork.getMultilayerPerceptron().setHiddenLayer(HIDDEN_LAYER);
        neuralNetwork.getMultilayerPerceptron().setActivationFunction(abstractFunction);
        return neuralNetwork;
    }

    /**
     * Creates random forests classifier.
     *
     * @param decisionTreeType - decision tree type
     * @return random forests classifier
     */
    public static RandomForests createRandomForests(DecisionTreeType decisionTreeType) {
        RandomForests randomForests = new RandomForests();
        randomForests.setSeed(SEED);
        randomForests.setNumThreads(NUM_THREADS);
        randomForests.setNumIterations(NUM_ITERATIONS);
        randomForests.setDecisionTreeType(decisionTreeType);
        randomForests.setMaxDepth(MAX_DEPTH);
        randomForests.setMinObj(NUM_OBJ);
        randomForests.setNumRandomAttr(NUM_RANDOM_ATTR);
        return randomForests;
    }

    /**
     * Creates extra trees classifier.
     *
     * @param decisionTreeType - decision tree type
     * @return extra trees classifier
     */
    public static ExtraTreesClassifier createExtraTreesClassifier(DecisionTreeType decisionTreeType) {
        ExtraTreesClassifier treesClassifier = new ExtraTreesClassifier();
        treesClassifier.setSeed(SEED);
        treesClassifier.setNumThreads(NUM_THREADS);
        treesClassifier.setNumIterations(NUM_ITERATIONS);
        treesClassifier.setDecisionTreeType(decisionTreeType);
        treesClassifier.setMaxDepth(MAX_DEPTH);
        treesClassifier.setMinObj(NUM_OBJ);
        treesClassifier.setNumRandomAttr(NUM_RANDOM_ATTR);
        treesClassifier.setUseBootstrapSamples(true);
        treesClassifier.setNumRandomSplits(NUM_RANDOM_SPLITS);
        return treesClassifier;
    }

    /**
     * Creates random networks object.
     *
     * @return random networks object
     */
    public static RandomNetworks createRandomNetworks() {
        RandomNetworks randomNetworks = new RandomNetworks();
        randomNetworks.setSeed(SEED);
        randomNetworks.setNumThreads(NUM_THREADS);
        randomNetworks.setUseBootstrapSamples(true);
        randomNetworks.setNumIterations(NUM_ITERATIONS);
        randomNetworks.setMinError(MIN_ERROR_THRESHOLD);
        randomNetworks.setMaxError(MAX_ERROR_THRESHOLD);
        return randomNetworks;
    }

    /**
     * Creates stacking classifier.
     *
     * @return stacking classifier
     */
    public static StackingClassifier createStackingClassifier() {
        StackingClassifier stackingClassifier = new StackingClassifier();
        stackingClassifier.setSeed(SEED);
        stackingClassifier.setUseCrossValidation(true);
        stackingClassifier.setNumFolds(NUM_FOLDS);
        return stackingClassifier;
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
     * Creates random networks options.
     *
     * @return random networks options
     */
    public static RandomNetworkOptions createRandomNetworkOptions() {
        RandomNetworkOptions randomNetworkOptions = new RandomNetworkOptions();
        randomNetworkOptions.setUseBootstrapSamples(true);
        randomNetworkOptions.setNumIterations(NUM_ITERATIONS);
        randomNetworkOptions.setSeed(SEED);
        randomNetworkOptions.setMinError(MIN_ERROR_THRESHOLD);
        randomNetworkOptions.setMaxError(MAX_ERROR_THRESHOLD);
        return randomNetworkOptions;
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
        classifierReport.setInputOptionsMap(new InputOptionsMap());
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
        evaluationMethodReport.setEvaluationMethod(com.ecaservice.dto.evaluation.EvaluationMethod.CROSS_VALIDATION);
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
        return experimentResultsEntity;
    }

    /**
     * Creates experiment results request.
     *
     * @param experimentResultsEntity - experiment results entity
     * @param experiment              - experiment entity
     * @param responseStatus          - response status
     * @return experiment results request
     */
    public static ExperimentResultsRequest createExperimentResultsRequest(
            ExperimentResultsEntity experimentResultsEntity,
            Experiment experiment,
            ErsResponseStatus responseStatus) {
        ExperimentResultsRequest experimentResultsRequest = new ExperimentResultsRequest();
        experimentResultsRequest.setExperimentResults(experimentResultsEntity);
        experimentResultsRequest.setExperiment(experiment);
        experimentResultsRequest.setResponseStatus(responseStatus);
        experimentResultsRequest.setRequestDate(LocalDateTime.now());
        experimentResultsRequest.setRequestId(UUID.randomUUID().toString());
        experimentResultsRequest.setRequestSource(ExperimentResultsRequestSource.SYSTEM);
        return experimentResultsRequest;
    }

    /**
     * Creates filter dictionary value.
     *
     * @return filter dictionary value
     */
    public static FilterDictionaryValue createFilterDictionaryValue() {
        FilterDictionaryValue filterDictionaryValue = new FilterDictionaryValue();
        filterDictionaryValue.setLabel(LABEL);
        filterDictionaryValue.setValue(VALUE);
        return filterDictionaryValue;
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
    public static GetEvaluationResultsResponse createGetEvaluationResultsSimpleResponse(String requestId,
                                                                                        ResponseStatus responseStatus) {
        GetEvaluationResultsResponse resultsSimpleResponse = new GetEvaluationResultsResponse();
        resultsSimpleResponse.setRequestId(requestId);
        resultsSimpleResponse.setStatus(responseStatus);
        return resultsSimpleResponse;
    }
}
