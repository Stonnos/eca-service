package com.ecaservice.ers.service;

import com.ecaservice.ers.AbstractJpaTest;
import com.ecaservice.ers.config.ErsConfig;
import com.ecaservice.ers.dto.ClassifierOptionsRequest;
import com.ecaservice.ers.dto.EvaluationMethod;
import com.ecaservice.ers.exception.DataNotFoundException;
import com.ecaservice.ers.model.EvaluationResultsInfo;
import com.ecaservice.ers.model.InstancesInfo;
import com.ecaservice.ers.repository.EvaluationResultsInfoRepository;
import com.ecaservice.ers.repository.InstancesInfoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static com.ecaservice.ers.TestHelperUtils.buildInstancesInfo;
import static com.ecaservice.ers.TestHelperUtils.createClassifierOptionsRequest;
import static com.ecaservice.ers.TestHelperUtils.createEvaluationResultsInfo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit tests for checking {@link ClassifierOptionsService} functionality.
 *
 * @author Roman Batygin
 */
@Import({ClassifierOptionsService.class, ErsConfig.class, SortFieldService.class})
class ClassifierOptionsServiceTest extends AbstractJpaTest {

    @Autowired
    private InstancesInfoRepository instancesInfoRepository;
    @Autowired
    private EvaluationResultsInfoRepository evaluationResultsInfoRepository;
    @Autowired
    private ClassifierOptionsService classifierOptionsService;
    @Autowired
    private ErsConfig ersConfig;

    @Override
    public void deleteAll() {
        evaluationResultsInfoRepository.deleteAll();
        instancesInfoRepository.deleteAll();
    }

    @Test
    void testDataNotFoundException() {
        ClassifierOptionsRequest request = createClassifierOptionsRequest(EvaluationMethod.TRAINING_DATA);
        assertThrows(DataNotFoundException.class, () -> classifierOptionsService.findBestClassifierOptions(request));
    }

    @Test
    void testClassifierOptionsSearchingWithTrainingDataEvaluationMethod() {
        testClassifierOptionsSearching(createClassifierOptionsRequest(EvaluationMethod.TRAINING_DATA));
    }

    @Test
    void testClassifierOptionsSearchingWithCrossValidation() {
        testClassifierOptionsSearching(createClassifierOptionsRequest(EvaluationMethod.CROSS_VALIDATION));
    }

    @Test
    void testClassifierOptionsSearchingWithTrainingDataEvaluationMethodAndDefaultSortFields() {
        ClassifierOptionsRequest request = createClassifierOptionsRequest(EvaluationMethod.TRAINING_DATA);
        request.getEvaluationResultsStatisticsSortFields().clear();
        testClassifierOptionsSearching(request);
    }

    @Test
    void testClassifierOptionsSearchingWithCrossValidationAndDefaultSortFields() {
        ClassifierOptionsRequest request = createClassifierOptionsRequest(EvaluationMethod.CROSS_VALIDATION);
        request.getEvaluationResultsStatisticsSortFields().clear();
        testClassifierOptionsSearching(request);
    }

    private void testClassifierOptionsSearching(ClassifierOptionsRequest request) {
        EvaluationMethod evaluationMethod = request.getEvaluationMethodReport().getEvaluationMethod();
        InstancesInfo instancesInfo = buildInstancesInfo();
        instancesInfo.setUuid(request.getDataUuid());
        InstancesInfo anotherInstancesInfo = buildInstancesInfo();
        instancesInfoRepository.saveAll(Arrays.asList(instancesInfo, anotherInstancesInfo));

        EvaluationResultsInfo evaluationResultsInfo1 = createEvaluationResultsInfo(instancesInfo,
                "Classifier1", evaluationMethod, BigDecimal.valueOf(67.73d),
                BigDecimal.valueOf(0.76d), BigDecimal.valueOf(0.07d));
        EvaluationResultsInfo evaluationResultsInfo2 = createEvaluationResultsInfo(instancesInfo,
                "Classifier2", evaluationMethod, BigDecimal.valueOf(65.96d),
                BigDecimal.valueOf(0.72d), BigDecimal.valueOf(0.046d));
        EvaluationResultsInfo evaluationResultsInfo3 = createEvaluationResultsInfo(instancesInfo,
                "Classifier3", evaluationMethod, BigDecimal.valueOf(61.08d),
                BigDecimal.valueOf(0.73d), BigDecimal.valueOf(0.006d));
        EvaluationResultsInfo evaluationResultsInfo4 = createEvaluationResultsInfo(instancesInfo,
                "Classifier4", evaluationMethod, BigDecimal.valueOf(87.79d),
                BigDecimal.valueOf(0.71d), BigDecimal.valueOf(0.01d));
        EvaluationResultsInfo evaluationResultsInfo5 = createEvaluationResultsInfo(instancesInfo,
                "Classifier5", evaluationMethod, BigDecimal.valueOf(87.79d),
                BigDecimal.valueOf(0.79d), BigDecimal.valueOf(0.04d));
        EvaluationResultsInfo evaluationResultsInfo6 = createEvaluationResultsInfo(anotherInstancesInfo,
                "Classifier6", evaluationMethod, BigDecimal.valueOf(56.80d),
                BigDecimal.valueOf(0.88d), BigDecimal.valueOf(0.09d));
        EvaluationResultsInfo evaluationResultsInfo7 = createEvaluationResultsInfo(instancesInfo,
                "Classifier7", evaluationMethod, BigDecimal.valueOf(87.79d),
                BigDecimal.valueOf(0.81d), BigDecimal.valueOf(0.03d));
        evaluationResultsInfoRepository.saveAll(
                Arrays.asList(evaluationResultsInfo1, evaluationResultsInfo2, evaluationResultsInfo3,
                        evaluationResultsInfo4, evaluationResultsInfo5, evaluationResultsInfo6,
                        evaluationResultsInfo7));

        List<EvaluationResultsInfo> bestClassifierOptions =
                classifierOptionsService.findBestClassifierOptions(request);
        assertThat(bestClassifierOptions).isNotEmpty();
        assertThat(bestClassifierOptions).hasSize(ersConfig.getResultSize());
        assertThat(bestClassifierOptions.get(0).getClassifierName()).isEqualTo("Classifier7");
        assertThat(bestClassifierOptions.get(1).getClassifierName()).isEqualTo("Classifier5");
        assertThat(bestClassifierOptions.get(2).getClassifierName()).isEqualTo("Classifier4");
    }
}
