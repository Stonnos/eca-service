package com.ecaservice.server.service.classifiers;

import com.ecaservice.classifier.options.model.ClassifierOptions;
import com.ecaservice.core.form.template.service.FormTemplateProvider;
import com.ecaservice.web.dto.model.FormTemplateDto;
import com.ecaservice.web.dto.model.InputOptionDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.IntStream;

import static com.ecaservice.server.TestHelperUtils.createDecisionTreeOptions;
import static com.ecaservice.server.TestHelperUtils.createJ48Options;
import static com.ecaservice.server.TestHelperUtils.createKNearestNeighboursOptions;
import static com.ecaservice.server.TestHelperUtils.createLogisticOptions;
import static com.ecaservice.server.TestHelperUtils.createNeuralNetworkOptions;
import static com.ecaservice.server.TestHelperUtils.loadClassifiersTemplates;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link ClassifiersTemplateService} class.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
@Import(ClassifiersTemplateService.class)
class ClassifiersTemplateServiceTest {

    private static final String CLASSIFIERS = "classifiers";

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
    private static final int NETWORK_SEED_IDX = 8;
    private static final int DECISION_TREE_USE_RANDOM_SPLITS_IDX = 6;
    private static final int DECISION_TREE_NUM_RANDOM_SPLITS_IDX = 7;
    private static final int DECISION_TREE_SEED_IDX = 8;
    private static final int NETWORK_HIDDEN_LAYER_IDX = 2;
    private static final int NETWORK_NUM_ITS_IDX = 3;
    private static final int NETWORK_MIN_ERROR_IDX = 4;
    private static final int NETWORK_AF_TYPE_IDX = 5;
    private static final int NETWORK_LEARNING_RATE_IDX = 6;
    private static final int NETWORK_MOMENTUM_IDX = 7;
    private static final int NETWORK_NUM_IN_NEURONS_IDX = 0;
    private static final int NETWORK_NUM_OUT_NEURONS_IDX = 1;

    @MockBean
    private FormTemplateProvider formTemplateProvider;

    @Inject
    private ClassifiersTemplateService classifiersTemplateService;

    private ObjectMapper objectMapper = new ObjectMapper();

    private List<FormTemplateDto> templates;

    @BeforeEach
    void init() {
        templates = loadClassifiersTemplates();
        when(formTemplateProvider.getTemplates(CLASSIFIERS)).thenReturn(templates);
    }

    @Test
    void testGetTemplates() {
        var result = classifiersTemplateService.getClassifiersTemplates();
        assertThat(result).hasSameSizeAs(templates);
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
                            String.valueOf(kNearestNeighboursOptions.getWeight()));
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
                case NETWORK_NUM_IN_NEURONS_IDX:
                    assertThat(inputOptions.get(i).getOptionValue()).isEqualTo(
                            String.valueOf(neuralNetworkOptions.getNumInNeurons()));
                    break;
                case NETWORK_NUM_OUT_NEURONS_IDX:
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
                            String.valueOf(neuralNetworkOptions.getMinError()));
                    break;
                case NETWORK_AF_TYPE_IDX:
                    assertThat(inputOptions.get(i).getOptionValue()).isEqualTo(
                            neuralNetworkOptions.getActivationFunctionOptions().getActivationFunctionType().getDescription());
                    break;
                case NETWORK_LEARNING_RATE_IDX:
                    assertThat(inputOptions.get(i).getOptionValue()).isEqualTo(
                            String.valueOf(neuralNetworkOptions.getBackPropagationOptions().getLearningRate()));
                    break;
                case NETWORK_MOMENTUM_IDX:
                    assertThat(inputOptions.get(i).getOptionValue()).isEqualTo(
                            String.valueOf(neuralNetworkOptions.getBackPropagationOptions().getMomentum()));
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

    private List<InputOptionDto> parseInputOptions(ClassifierOptions classifierOptions) throws JsonProcessingException {
        String templateName = classifierOptions.getClass().getSimpleName();
        var templateDto = getTemplate(templateName);
        when(formTemplateProvider.getTemplate(templateName)).thenReturn(templateDto);
        var json = objectMapper.writeValueAsString(classifierOptions);
        return classifiersTemplateService.processInputOptions(json);
    }

    private FormTemplateDto getTemplate(String templateName) {
        return templates.stream()
                .filter(formTemplateDto -> formTemplateDto.getTemplateName().equals(templateName))
                .findFirst()
                .orElseThrow(
                        () -> new IllegalArgumentException(String.format("Can't get template [%s]", templateName)));
    }
}
