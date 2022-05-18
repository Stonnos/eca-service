package com.ecaservice.server.service.classifiers;

import com.ecaservice.classifier.options.config.ClassifiersOptionsConfig;
import com.ecaservice.classifier.options.model.ClassifierOptions;
import com.ecaservice.core.filter.service.FilterService;
import com.ecaservice.core.form.template.service.FormTemplateProvider;
import com.ecaservice.server.mapping.ClassifierInfoMapperImpl;
import com.ecaservice.web.dto.model.ClassifierInfoDto;
import com.ecaservice.web.dto.model.FormTemplateDto;
import com.ecaservice.web.dto.model.InputOptionDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eca.ensemble.forests.DecisionTreeType;
import eca.text.NumericFormatFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;
import java.text.DecimalFormat;
import java.util.List;
import java.util.stream.IntStream;

import static com.ecaservice.server.TestHelperUtils.createAdaBoostOptions;
import static com.ecaservice.server.TestHelperUtils.createClassifierInfo;
import static com.ecaservice.server.TestHelperUtils.createDecisionTreeOptions;
import static com.ecaservice.server.TestHelperUtils.createExtraTreesOptions;
import static com.ecaservice.server.TestHelperUtils.createFilterDictionaryDto;
import static com.ecaservice.server.TestHelperUtils.createHeterogeneousClassifierOptions;
import static com.ecaservice.server.TestHelperUtils.createJ48Options;
import static com.ecaservice.server.TestHelperUtils.createKNearestNeighboursOptions;
import static com.ecaservice.server.TestHelperUtils.createLogisticOptions;
import static com.ecaservice.server.TestHelperUtils.createNeuralNetworkOptions;
import static com.ecaservice.server.TestHelperUtils.createRandomForestsOptions;
import static com.ecaservice.server.TestHelperUtils.createStackingOptions;
import static com.ecaservice.server.TestHelperUtils.loadClassifiersTemplates;
import static com.ecaservice.server.TestHelperUtils.loadEnsembleClassifiersTemplates;
import static com.ecaservice.server.service.filter.dictionary.FilterDictionaries.CLASSIFIER_NAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link ClassifiersTemplateProvider} class.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
@EnableConfigurationProperties
@TestPropertySource("classpath:application.properties")
@Import({ClassifiersTemplateProvider.class, ClassifierOptionsProcessor.class, ClassifierInfoMapperImpl.class,
        ClassifiersOptionsConfig.class})
class ClassifierOptionsProcessorTest {

    private static final String CLASSIFIERS = "classifiers";
    private static final String ENSEMBLE_CLASSIFIERS = "ensembleClassifiers";
    private static final DecimalFormat DECIMAL_FORMAT = NumericFormatFactory.getInstance(Integer.MAX_VALUE);
    private static final int LOGISTIC_MAX_ITS_IDX = 0;
    private static final int LOGISTIC_USE_CONJUGATE_GRADIENT_DESCENT_IDX = 1;
    private static final int KNN_DISTANCE_IDX = 2;
    private static final int KNN_NUM_NEIGHBOURS_IDX = 0;
    private static final int KNN_WEIGHT_IDX = 1;
    private static final int J48_MIN_OBJ_IDX = 0;
    private static final int J48_BINARY_SPLIT_IDX = 1;
    private static final int J48_UNPRUNED_IDX = 2;
    private static final int J48_NUM_FOLDS_IDX = 3;
    private static final int DECISION_TREE_TYPE_IDX = 0;
    private static final int DECISION_TREE_MIN_OBJ_IDX = 1;
    private static final int DECISION_TREE_MAX_DEPTH_IDX = 2;
    private static final int DECISION_TREE__USE_BINARY_SPLITS_IDX = 3;
    private static final int DECISION_TREE_RANDOM_TREE_IDX = 4;
    private static final int DECISION_TREE_NUM_RANDOM_ATTRS_IDX = 5;
    private static final int DECISION_TREE_USE_RANDOM_SPLITS_IDX = 6;
    private static final int DECISION_TREE_NUM_RANDOM_SPLITS_IDX = 7;
    private static final int DECISION_TREE_SEED_IDX = 8;
    private static final int NETWORK_NUM_IN_NEURONS = 0;
    private static final int NETWORK_NUM_OUT_NEURONS = 1;
    private static final int NETWORK_HIDDEN_LAYER_IDX = 2;
    private static final int NETWORK_NUM_ITS_IDX = 3;
    private static final int NETWORK_MIN_ERROR_IDX = 4;
    private static final int NETWORK_AF_TYPE_IDX = 5;
    private static final int NETWORK_LEARNING_RATE_IDX = 6;
    private static final int NETWORK_MOMENTUM_IDX = 7;
    private static final int NETWORK_SEED_IDX = 8;
    private static final int ADA_BOOST_NUM_ITS_IDX = 0;
    private static final int ADA_BOOST_MIN_ERROR_IDX = 1;
    private static final int ADA_BOOST_MAX_ERROR_IDX = 2;
    private static final int ADA_BOOST_SEED_IDX = 3;
    private static final int HEC_NUM_ITS_IDX = 0;
    private static final int HEC_NUM_THREADS_IDX = 1;
    private static final int HEC_MIN_ERROR_IDX = 2;
    private static final int HEC_MAX_ERROR_IDX = 3;
    private static final int HEC_USE_WEIGHTED_VOTES_IDX = 4;
    private static final int HEC_USE_RANDOM_CLASSIFIER_IDX = 5;
    private static final int HEC_SAMPLING_METHOD_IDX = 6;
    private static final int HEC_USE_RANDOM_SUBSPACES_IDX = 7;
    private static final int HEC_SEED_IDX = 8;
    private static final int STACKING_USE_CV_IDX = 0;
    private static final int STACKING_NUM_FOLDS_IDX = 1;
    private static final int STACKING_SEED_IDX = 2;
    private static final int RANDOM_FORESTS_TREE_TYPE_IDX = 0;
    private static final int RANDOM_FORESTS_NUM_ITS_IDX = 1;
    private static final int RANDOM_FORESTS_MIN_OBJ_IDX = 2;
    private static final int RANDOM_FORESTS_MAX_DEPTH_IDX = 3;
    private static final int RANDOM_FORESTS_NUM_RANDOM_ATTR_IDX = 4;
    private static final int RANDOM_FORESTS_NUM_THREADS_IDX = 5;
    private static final int RANDOM_FORESTS_SEED_IDX = 6;
    private static final int EXTRA_TREES_TREE_TYPE_IDX = 0;
    private static final int EXTRA_TREES_NUM_ITS_IDX = 1;
    private static final int EXTRA_TREES_MIN_OBJ_IDX = 2;
    private static final int EXTRA_TREES_MAX_DEPTH_IDX = 3;
    private static final int EXTRA_TREES_NUM_RANDOM_ATTR_IDX = 4;
    private static final int EXTRA_TREES_NUM_THREADS_IDX = 5;
    private static final int EXTRA_TREES_NUM_RANDOM_SPLITS_IDX = 6;
    private static final int EXTRA_TREES_USE_BOOTSTRAP_SAMPLES_IDX = 7;
    private static final int EXTRA_TREES_SEED_IDX = 8;

    @MockBean
    private FormTemplateProvider formTemplateProvider;
    @MockBean
    private FilterService filterService;

    @Inject
    private ClassifierOptionsProcessor classifierOptionsProcessor;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void init() {
        List<FormTemplateDto> templates = loadClassifiersTemplates();
        List<FormTemplateDto> ensembleTemplates = loadEnsembleClassifiersTemplates();
        when(formTemplateProvider.getTemplates(CLASSIFIERS)).thenReturn(templates);
        when(formTemplateProvider.getTemplates(ENSEMBLE_CLASSIFIERS)).thenReturn(ensembleTemplates);
        when(filterService.getFilterDictionary(CLASSIFIER_NAME)).thenReturn(createFilterDictionaryDto());
        classifierOptionsProcessor.initialize();
    }

    @Test
    void testParseLogisticOptions() throws JsonProcessingException {
        var logisticOptions = createLogisticOptions();
        var inputOptions = parseInputOptions(logisticOptions);
        assertThat(inputOptions).isNotEmpty();
        IntStream.range(0, inputOptions.size()).forEach(i -> {
            switch (i) {
                case LOGISTIC_MAX_ITS_IDX:
                    assertThat(inputOptions.get(i).getOptionValue()).isEqualTo(
                            String.valueOf(logisticOptions.getMaxIts()));
                    break;
                case LOGISTIC_USE_CONJUGATE_GRADIENT_DESCENT_IDX:
                    assertThat(inputOptions.get(i).getOptionValue()).isEqualTo(
                            String.valueOf(logisticOptions.getUseConjugateGradientDescent()));
                    break;
                default:
                    fail(String.format("Can't assert input options at index [%d] for classifier [%s]", i,
                            logisticOptions.getClass().getSimpleName()));
            }
        });
    }

    @Test
    void testParseKnnOptions() throws JsonProcessingException {
        var kNearestNeighboursOptions = createKNearestNeighboursOptions();
        var inputOptions = parseInputOptions(kNearestNeighboursOptions);
        assertThat(inputOptions).isNotEmpty();
        IntStream.range(0, inputOptions.size()).forEach(i -> {
            switch (i) {
                case KNN_DISTANCE_IDX:
                    assertThat(inputOptions.get(i).getOptionValue()).isEqualTo(
                            kNearestNeighboursOptions.getDistanceType().getDescription());
                    break;
                case KNN_NUM_NEIGHBOURS_IDX:
                    assertThat(inputOptions.get(i).getOptionValue()).isEqualTo(
                            String.valueOf(kNearestNeighboursOptions.getNumNeighbours()));
                    break;
                case KNN_WEIGHT_IDX:
                    assertThat(inputOptions.get(i).getOptionValue()).isEqualTo(
                            DECIMAL_FORMAT.format(kNearestNeighboursOptions.getWeight()));
                    break;
                default:
                    fail(String.format("Can't assert input options at index [%d] for classifier [%s]", i,
                            kNearestNeighboursOptions.getClass().getSimpleName()));
            }
        });
    }

    @Test
    void testParseJ48Options() throws JsonProcessingException {
        var j48Options = createJ48Options();
        var inputOptions = parseInputOptions(j48Options);
        assertThat(inputOptions).isNotEmpty();
        IntStream.range(0, inputOptions.size()).forEach(i -> {
            switch (i) {
                case J48_MIN_OBJ_IDX:
                    assertThat(inputOptions.get(i).getOptionValue()).isEqualTo(
                            String.valueOf(j48Options.getMinNumObj()));
                    break;
                case J48_BINARY_SPLIT_IDX:
                    assertThat(inputOptions.get(i).getOptionValue()).isEqualTo(
                            String.valueOf(j48Options.getBinarySplits()));
                    break;
                case J48_UNPRUNED_IDX:
                    assertThat(inputOptions.get(i).getOptionValue()).isEqualTo(
                            String.valueOf(j48Options.getUnpruned()));
                    break;
                case J48_NUM_FOLDS_IDX:
                    assertThat(inputOptions.get(i).getOptionValue()).isEqualTo(
                            String.valueOf(j48Options.getNumFolds()));
                    break;
                default:
                    fail(String.format("Can't assert input options at index [%d] for classifier [%s]", i,
                            j48Options.getClass().getSimpleName()));
            }
        });
    }

    @Test
    void testParseDecisionTreeOptions() throws JsonProcessingException {
        var decisionTreeOptions = createDecisionTreeOptions();
        var inputOptions = parseInputOptions(decisionTreeOptions);
        assertThat(inputOptions).isNotEmpty();
        IntStream.range(0, inputOptions.size()).forEach(i -> {
            switch (i) {
                case DECISION_TREE_TYPE_IDX:
                    assertThat(inputOptions.get(i).getOptionValue()).isEqualTo(
                            decisionTreeOptions.getDecisionTreeType().getDescription());
                    break;
                case DECISION_TREE_MIN_OBJ_IDX:
                    assertThat(inputOptions.get(i).getOptionValue()).isEqualTo(
                            String.valueOf(decisionTreeOptions.getMinObj()));
                    break;
                case DECISION_TREE_MAX_DEPTH_IDX:
                    assertThat(inputOptions.get(i).getOptionValue()).isEqualTo(
                            String.valueOf(decisionTreeOptions.getMaxDepth()));
                    break;
                case DECISION_TREE__USE_BINARY_SPLITS_IDX:
                    assertThat(inputOptions.get(i).getOptionValue()).isEqualTo(
                            String.valueOf(decisionTreeOptions.getUseBinarySplits()));
                    break;
                case DECISION_TREE_RANDOM_TREE_IDX:
                    assertThat(inputOptions.get(i).getOptionValue()).isEqualTo(
                            String.valueOf(decisionTreeOptions.getRandomTree()));
                    break;
                case DECISION_TREE_NUM_RANDOM_ATTRS_IDX:
                    assertThat(inputOptions.get(i).getOptionValue()).isEqualTo(
                            String.valueOf(decisionTreeOptions.getNumRandomAttr()));
                    break;
                case DECISION_TREE_USE_RANDOM_SPLITS_IDX:
                    assertThat(inputOptions.get(i).getOptionValue()).isEqualTo(
                            String.valueOf(decisionTreeOptions.getUseRandomSplits()));
                    break;
                case DECISION_TREE_NUM_RANDOM_SPLITS_IDX:
                    assertThat(inputOptions.get(i).getOptionValue()).isEqualTo(
                            String.valueOf(decisionTreeOptions.getNumRandomSplits()));
                    break;
                case DECISION_TREE_SEED_IDX:
                    assertThat(inputOptions.get(i).getOptionValue()).isEqualTo(
                            String.valueOf(decisionTreeOptions.getSeed()));
                    break;
                default:
                    fail(String.format("Can't assert input options at index [%d] for classifier [%s]", i,
                            decisionTreeOptions.getClass().getSimpleName()));
            }
        });
    }

    @Test
    void testParseNeuralNetworkOptions() throws JsonProcessingException {
        var neuralNetworkOptions = createNeuralNetworkOptions();
        var inputOptions = parseInputOptions(neuralNetworkOptions);
        assertThat(inputOptions).isNotEmpty();
        IntStream.range(0, inputOptions.size()).forEach(i -> {
            switch (i) {
                case NETWORK_NUM_IN_NEURONS:
                    assertThat(inputOptions.get(i).getOptionValue()).isEqualTo(
                            String.valueOf(neuralNetworkOptions.getNumInNeurons()));
                    break;
                case NETWORK_NUM_OUT_NEURONS:
                    assertThat(inputOptions.get(i).getOptionValue()).isEqualTo(
                            String.valueOf(neuralNetworkOptions.getNumOutNeurons()));
                    break;
                case NETWORK_HIDDEN_LAYER_IDX:
                    assertThat(inputOptions.get(i).getOptionValue()).isEqualTo(
                            neuralNetworkOptions.getHiddenLayer());
                    break;
                case NETWORK_NUM_ITS_IDX:
                    assertThat(inputOptions.get(i).getOptionValue()).isEqualTo(
                            String.valueOf(neuralNetworkOptions.getNumIterations()));
                    break;
                case NETWORK_MIN_ERROR_IDX:
                    assertThat(inputOptions.get(i).getOptionValue()).isEqualTo(
                            DECIMAL_FORMAT.format(neuralNetworkOptions.getMinError()));
                    break;
                case NETWORK_AF_TYPE_IDX:
                    assertThat(inputOptions.get(i).getOptionValue()).isEqualTo(
                            neuralNetworkOptions.getActivationFunctionOptions().getActivationFunctionType().getDescription());
                    break;
                case NETWORK_LEARNING_RATE_IDX:
                    assertThat(inputOptions.get(i).getOptionValue()).isEqualTo(
                            DECIMAL_FORMAT.format(neuralNetworkOptions.getBackPropagationOptions().getLearningRate()));
                    break;
                case NETWORK_MOMENTUM_IDX:
                    assertThat(inputOptions.get(i).getOptionValue()).isEqualTo(
                            DECIMAL_FORMAT.format(neuralNetworkOptions.getBackPropagationOptions().getMomentum()));
                    break;
                case NETWORK_SEED_IDX:
                    assertThat(inputOptions.get(i).getOptionValue()).isEqualTo(
                            String.valueOf(neuralNetworkOptions.getSeed()));
                    break;
                default:
                    fail(String.format("Can't assert input options at index [%d] for classifier [%s]", i,
                            neuralNetworkOptions.getClass().getSimpleName()));
            }
        });
    }

    @Test
    void testParseAdaBoost() {
        var adaBoostOptions = createAdaBoostOptions();
        var classifierInfoDto = processClassifierOptions(adaBoostOptions);
        assertThat(classifierInfoDto).isNotNull();
        assertThat(classifierInfoDto.getInputOptions()).isNotEmpty();
        var inputOptions = classifierInfoDto.getInputOptions();
        assertThat(classifierInfoDto.getIndividualClassifiers()).hasSameSizeAs(
                adaBoostOptions.getClassifierOptions());
        IntStream.range(0, classifierInfoDto.getInputOptions().size()).forEach(i -> {
            switch (i) {
                case ADA_BOOST_NUM_ITS_IDX:
                    assertThat(inputOptions.get(i).getOptionValue()).isEqualTo(
                            String.valueOf(adaBoostOptions.getNumIterations()));
                    break;
                case ADA_BOOST_MIN_ERROR_IDX:
                    assertThat(inputOptions.get(i).getOptionValue()).isEqualTo(
                            DECIMAL_FORMAT.format(adaBoostOptions.getMinError()));
                    break;
                case ADA_BOOST_MAX_ERROR_IDX:
                    assertThat(inputOptions.get(i).getOptionValue()).isEqualTo(
                            DECIMAL_FORMAT.format(adaBoostOptions.getMaxError()));
                    break;
                case ADA_BOOST_SEED_IDX:
                    assertThat(inputOptions.get(i).getOptionValue()).isEqualTo(
                            String.valueOf(adaBoostOptions.getSeed()));
                    break;
                default:
                    fail(String.format("Can't assert input options at index [%d] for classifier [%s]", i,
                            adaBoostOptions.getClass().getSimpleName()));
            }
        });
    }

    @Test
    void testParseHeterogeneousClassifier() {
        var heterogeneousClassifierOptions = createHeterogeneousClassifierOptions(true);
        var classifierInfoDto = processClassifierOptions(heterogeneousClassifierOptions);
        assertThat(classifierInfoDto).isNotNull();
        assertThat(classifierInfoDto.getInputOptions()).isNotEmpty();
        var inputOptions = classifierInfoDto.getInputOptions();
        assertThat(classifierInfoDto.getIndividualClassifiers()).hasSameSizeAs(
                heterogeneousClassifierOptions.getClassifierOptions());
        IntStream.range(0, classifierInfoDto.getInputOptions().size()).forEach(i -> {
            switch (i) {
                case HEC_NUM_ITS_IDX:
                    assertThat(inputOptions.get(i).getOptionValue()).isEqualTo(
                            String.valueOf(heterogeneousClassifierOptions.getNumIterations()));
                    break;
                case HEC_NUM_THREADS_IDX:
                    assertThat(inputOptions.get(i).getOptionValue()).isEqualTo(
                            String.valueOf(heterogeneousClassifierOptions.getNumThreads()));
                    break;
                case HEC_MIN_ERROR_IDX:
                    assertThat(inputOptions.get(i).getOptionValue()).isEqualTo(
                            DECIMAL_FORMAT.format(heterogeneousClassifierOptions.getMinError()));
                    break;
                case HEC_MAX_ERROR_IDX:
                    assertThat(inputOptions.get(i).getOptionValue()).isEqualTo(
                            DECIMAL_FORMAT.format(heterogeneousClassifierOptions.getMaxError()));
                    break;
                case HEC_USE_WEIGHTED_VOTES_IDX:
                    assertThat(inputOptions.get(i).getOptionValue()).isEqualTo(
                            String.valueOf(heterogeneousClassifierOptions.getUseWeightedVotes()));
                    break;
                case HEC_USE_RANDOM_CLASSIFIER_IDX:
                    assertThat(inputOptions.get(i).getOptionValue()).isEqualTo(
                            String.valueOf(heterogeneousClassifierOptions.getUseRandomClassifier()));
                    break;
                case HEC_SAMPLING_METHOD_IDX:
                    assertThat(inputOptions.get(i).getOptionValue()).isEqualTo(
                            heterogeneousClassifierOptions.getSamplingMethod().getDescription());
                    break;
                case HEC_USE_RANDOM_SUBSPACES_IDX:
                    assertThat(inputOptions.get(i).getOptionValue()).isEqualTo(
                            String.valueOf(heterogeneousClassifierOptions.getUseRandomSubspaces()));
                    break;
                case HEC_SEED_IDX:
                    assertThat(inputOptions.get(i).getOptionValue()).isEqualTo(
                            String.valueOf(heterogeneousClassifierOptions.getSeed()));
                    break;
                default:
                    fail(String.format("Can't assert input options at index [%d] for classifier [%s]", i,
                            heterogeneousClassifierOptions.getClass().getSimpleName()));
            }
        });
    }

    @Test
    void testParseStacking() {
        var stackingOptions = createStackingOptions();
        var classifierInfoDto = processClassifierOptions(stackingOptions);
        assertThat(classifierInfoDto).isNotNull();
        assertThat(classifierInfoDto.getInputOptions()).isNotEmpty();
        var inputOptions = classifierInfoDto.getInputOptions();
        assertThat(classifierInfoDto.getIndividualClassifiers()).hasSize(
                stackingOptions.getClassifierOptions().size() + 1);
        IntStream.range(0, classifierInfoDto.getInputOptions().size()).forEach(i -> {
            switch (i) {
                case STACKING_USE_CV_IDX:
                    assertThat(inputOptions.get(i).getOptionValue()).isEqualTo(
                            String.valueOf(stackingOptions.getUseCrossValidation()));
                    break;
                case STACKING_NUM_FOLDS_IDX:
                    assertThat(inputOptions.get(i).getOptionValue()).isEqualTo(
                            String.valueOf(stackingOptions.getNumFolds()));
                    break;
                case STACKING_SEED_IDX:
                    assertThat(inputOptions.get(i).getOptionValue()).isEqualTo(
                            String.valueOf(stackingOptions.getSeed()));
                    break;
                default:
                    fail(String.format("Can't assert input options at index [%d] for classifier [%s]", i,
                            stackingOptions.getClass().getSimpleName()));
            }
        });
    }

    @Test
    void testParseRandomForests() {
        var randomForestsOptions = createRandomForestsOptions(DecisionTreeType.CART);
        var classifierInfoDto = processClassifierOptions(randomForestsOptions);
        assertThat(classifierInfoDto).isNotNull();
        assertThat(classifierInfoDto.getInputOptions()).isNotEmpty();
        var inputOptions = classifierInfoDto.getInputOptions();
        IntStream.range(0, classifierInfoDto.getInputOptions().size()).forEach(i -> {
            switch (i) {
                case RANDOM_FORESTS_TREE_TYPE_IDX:
                    assertThat(inputOptions.get(i).getOptionValue()).isEqualTo(
                            String.valueOf(randomForestsOptions.getDecisionTreeType().getDescription()));
                    break;
                case RANDOM_FORESTS_NUM_ITS_IDX:
                    assertThat(inputOptions.get(i).getOptionValue()).isEqualTo(
                            String.valueOf(randomForestsOptions.getNumIterations()));
                    break;
                case RANDOM_FORESTS_MIN_OBJ_IDX:
                    assertThat(inputOptions.get(i).getOptionValue()).isEqualTo(
                            String.valueOf(randomForestsOptions.getMinObj()));
                    break;
                case RANDOM_FORESTS_MAX_DEPTH_IDX:
                    assertThat(inputOptions.get(i).getOptionValue()).isEqualTo(
                            String.valueOf(randomForestsOptions.getMaxDepth()));
                    break;
                case RANDOM_FORESTS_NUM_RANDOM_ATTR_IDX:
                    assertThat(inputOptions.get(i).getOptionValue()).isEqualTo(
                            String.valueOf(randomForestsOptions.getNumRandomAttr()));
                    break;
                case RANDOM_FORESTS_NUM_THREADS_IDX:
                    assertThat(inputOptions.get(i).getOptionValue()).isEqualTo(
                            String.valueOf(randomForestsOptions.getNumThreads()));
                    break;
                case RANDOM_FORESTS_SEED_IDX:
                    assertThat(inputOptions.get(i).getOptionValue()).isEqualTo(
                            String.valueOf(randomForestsOptions.getSeed()));
                    break;
                default:
                    fail(String.format("Can't assert input options at index [%d] for classifier [%s]", i,
                            randomForestsOptions.getClass().getSimpleName()));
            }
        });
    }

    @Test
    void testParseExtraTrees() {
        var extraTreesOptions = createExtraTreesOptions(DecisionTreeType.CART);
        var classifierInfoDto = processClassifierOptions(extraTreesOptions);
        assertThat(classifierInfoDto).isNotNull();
        assertThat(classifierInfoDto.getInputOptions()).isNotEmpty();
        var inputOptions = classifierInfoDto.getInputOptions();
        IntStream.range(0, classifierInfoDto.getInputOptions().size()).forEach(i -> {
            switch (i) {
                case EXTRA_TREES_TREE_TYPE_IDX:
                    assertThat(inputOptions.get(i).getOptionValue()).isEqualTo(
                            String.valueOf(extraTreesOptions.getDecisionTreeType().getDescription()));
                    break;
                case EXTRA_TREES_NUM_ITS_IDX:
                    assertThat(inputOptions.get(i).getOptionValue()).isEqualTo(
                            String.valueOf(extraTreesOptions.getNumIterations()));
                    break;
                case EXTRA_TREES_MIN_OBJ_IDX:
                    assertThat(inputOptions.get(i).getOptionValue()).isEqualTo(
                            String.valueOf(extraTreesOptions.getMinObj()));
                    break;
                case EXTRA_TREES_MAX_DEPTH_IDX:
                    assertThat(inputOptions.get(i).getOptionValue()).isEqualTo(
                            String.valueOf(extraTreesOptions.getMaxDepth()));
                    break;
                case EXTRA_TREES_NUM_RANDOM_ATTR_IDX:
                    assertThat(inputOptions.get(i).getOptionValue()).isEqualTo(
                            String.valueOf(extraTreesOptions.getNumRandomAttr()));
                    break;
                case EXTRA_TREES_NUM_THREADS_IDX:
                    assertThat(inputOptions.get(i).getOptionValue()).isEqualTo(
                            String.valueOf(extraTreesOptions.getNumThreads()));
                    break;
                case EXTRA_TREES_NUM_RANDOM_SPLITS_IDX:
                    assertThat(inputOptions.get(i).getOptionValue()).isEqualTo(
                            String.valueOf(extraTreesOptions.getNumRandomSplits()));
                    break;
                case EXTRA_TREES_USE_BOOTSTRAP_SAMPLES_IDX:
                    assertThat(inputOptions.get(i).getOptionValue()).isEqualTo(
                            String.valueOf(extraTreesOptions.getUseBootstrapSamples()));
                    break;
                case EXTRA_TREES_SEED_IDX:
                    assertThat(inputOptions.get(i).getOptionValue()).isEqualTo(
                            String.valueOf(extraTreesOptions.getSeed()));
                    break;
                default:
                    fail(String.format("Can't assert input options at index [%d] for classifier [%s]", i,
                            extraTreesOptions.getClass().getSimpleName()));
            }
        });
    }

    private List<InputOptionDto> parseInputOptions(ClassifierOptions classifierOptions) throws JsonProcessingException {
        var json = objectMapper.writeValueAsString(classifierOptions);
        return classifierOptionsProcessor.processInputOptions(json);
    }

    private ClassifierInfoDto processClassifierOptions(ClassifierOptions classifierOptions) {
        var classifierInfo = createClassifierInfo(classifierOptions);
        return classifierOptionsProcessor.processClassifierInfo(classifierInfo);
    }
}
