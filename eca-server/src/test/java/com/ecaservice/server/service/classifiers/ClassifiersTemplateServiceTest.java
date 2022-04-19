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

import static com.ecaservice.server.TestHelperUtils.createJ48Options;
import static com.ecaservice.server.TestHelperUtils.createKNearestNeighboursOptions;
import static com.ecaservice.server.TestHelperUtils.createLogisticOptions;
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

    public static final int LOGISTIC_MAX_ITS_IDX = 0;
    public static final int LOGISTIC_USE_CONJUGATE_GRADIENT_DESCENT_IDX = 1;
    public static final int KNN_DISTANCE_IDX = 2;
    public static final int KNN_NUM_NEIGHBOURS_IDX = 0;
    public static final int KNN_WEIGHT_IDX = 1;
    public static final int J48_MIN_OBJ_IDX = 0;
    public static final int J48_BINARY_SPLIT_IDX = 1;
    public static final int J48_UNPRUNED_IDX = 2;
    public static final int J48_NUM_FOLDS_IDX = 3;

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
    void testParseKnn() throws JsonProcessingException {
        var kNearestNeighboursOptions = createKNearestNeighboursOptions();
        var inputOptions = parseInputOptions(kNearestNeighboursOptions);
        assertThat(inputOptions).isNotEmpty();
        IntStream.range(0, inputOptions.size()).forEach(i -> {
            switch (i) {
                case KNN_DISTANCE_IDX:
                    assertThat(inputOptions.get(i).getOptionValue()).isEqualTo(
                            kNearestNeighboursOptions.getDistanceType().name());
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
    void testParseJ48() throws JsonProcessingException {
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
