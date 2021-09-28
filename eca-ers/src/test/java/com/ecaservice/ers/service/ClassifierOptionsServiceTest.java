package com.ecaservice.ers.service;

import com.ecaservice.ers.AbstractJpaTest;
import com.ecaservice.ers.config.ErsConfig;
import com.ecaservice.ers.dto.ClassifierOptionsRequest;
import com.ecaservice.ers.dto.EvaluationMethod;
import com.ecaservice.ers.exception.DataNotFoundException;
import com.ecaservice.ers.model.ClassifierOptionsInfo;
import com.ecaservice.ers.model.EvaluationResultsInfo;
import com.ecaservice.ers.model.EvaluationResultsSortEntity;
import com.ecaservice.ers.model.InstancesInfo;
import com.ecaservice.ers.repository.EvaluationResultsInfoRepository;
import com.ecaservice.ers.repository.EvaluationResultsSortRepository;
import com.ecaservice.ers.repository.InstancesInfoRepository;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;
import org.springframework.util.DigestUtils;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import static com.ecaservice.ers.TestHelperUtils.buildClassifierOptionsInfo;
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

    @Inject
    private InstancesInfoRepository instancesInfoRepository;
    @Inject
    private EvaluationResultsInfoRepository evaluationResultsInfoRepository;
    @Inject
    private EvaluationResultsSortRepository evaluationResultsSortRepository;
    @Inject
    private ClassifierOptionsService classifierOptionsService;
    @Inject
    private ErsConfig ersConfig;

    @Override
    public void init() {
        evaluationResultsSortRepository.save(
                EvaluationResultsSortEntity.builder()
                        .fieldName("statistics.pctCorrect")
                        .ascending(false)
                        .fieldOrder(0).build()
        );
        evaluationResultsSortRepository.save(
                EvaluationResultsSortEntity.builder()
                        .fieldName("statistics.maxAucValue")
                        .ascending(false)
                        .fieldOrder(1).build()
        );
        evaluationResultsSortRepository.save(
                EvaluationResultsSortEntity.builder()
                        .fieldName("statistics.varianceError")
                        .ascending(true)
                        .fieldOrder(2).build()
        );
    }

    @Override
    public void deleteAll() {
        evaluationResultsInfoRepository.deleteAll();
        instancesInfoRepository.deleteAll();
        evaluationResultsSortRepository.deleteAll();
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
        request.getSortFields().clear();
        testClassifierOptionsSearching(request);
    }

    @Test
    void testClassifierOptionsSearchingWithCrossValidationAndDefaultSortFields() {
        ClassifierOptionsRequest request = createClassifierOptionsRequest(EvaluationMethod.CROSS_VALIDATION);
        request.getSortFields().clear();
        testClassifierOptionsSearching(request);
    }

    private void testClassifierOptionsSearching(ClassifierOptionsRequest request) {
        EvaluationMethod evaluationMethod = request.getEvaluationMethodReport().getEvaluationMethod();
        InstancesInfo instancesInfo = buildInstancesInfo();
        instancesInfo.setDataMd5Hash(request.getDataHash());
        InstancesInfo anotherInstancesInfo = buildInstancesInfo();
        anotherInstancesInfo.setDataMd5Hash(
                DigestUtils.md5DigestAsHex(StringUtils.EMPTY.getBytes(StandardCharsets.UTF_8)));
        instancesInfoRepository.saveAll(Arrays.asList(instancesInfo, anotherInstancesInfo));

        ClassifierOptionsInfo classifierOptionsInfo1 = buildClassifierOptionsInfo();
        classifierOptionsInfo1.setClassifierName("Classifier1");
        ClassifierOptionsInfo classifierOptionsInfo2 = buildClassifierOptionsInfo();
        classifierOptionsInfo2.setClassifierName("Classifier2");
        ClassifierOptionsInfo classifierOptionsInfo3 = buildClassifierOptionsInfo();
        classifierOptionsInfo3.setClassifierName("Classifier3");
        ClassifierOptionsInfo classifierOptionsInfo4 = buildClassifierOptionsInfo();
        classifierOptionsInfo4.setClassifierName("Classifier4");
        ClassifierOptionsInfo classifierOptionsInfo5 = buildClassifierOptionsInfo();
        classifierOptionsInfo5.setClassifierName("Classifier5");
        ClassifierOptionsInfo classifierOptionsInfo6 = buildClassifierOptionsInfo();
        classifierOptionsInfo6.setClassifierName("Classifier6");
        ClassifierOptionsInfo classifierOptionsInfo7 = buildClassifierOptionsInfo();
        classifierOptionsInfo7.setClassifierName("Classifier7");

        EvaluationResultsInfo evaluationResultsInfo1 = createEvaluationResultsInfo(instancesInfo,
                classifierOptionsInfo1, evaluationMethod, BigDecimal.valueOf(67.73d),
                BigDecimal.valueOf(0.76d), BigDecimal.valueOf(0.07d));
        EvaluationResultsInfo evaluationResultsInfo2 = createEvaluationResultsInfo(instancesInfo,
                classifierOptionsInfo2, evaluationMethod, BigDecimal.valueOf(65.96d),
                BigDecimal.valueOf(0.72d), BigDecimal.valueOf(0.046d));
        EvaluationResultsInfo evaluationResultsInfo3 = createEvaluationResultsInfo(instancesInfo,
                classifierOptionsInfo3, evaluationMethod, BigDecimal.valueOf(61.08d),
                BigDecimal.valueOf(0.73d), BigDecimal.valueOf(0.006d));
        EvaluationResultsInfo evaluationResultsInfo4 = createEvaluationResultsInfo(instancesInfo,
                classifierOptionsInfo4, evaluationMethod, BigDecimal.valueOf(87.79d),
                BigDecimal.valueOf(0.71d), BigDecimal.valueOf(0.01d));
        EvaluationResultsInfo evaluationResultsInfo5 = createEvaluationResultsInfo(instancesInfo,
                classifierOptionsInfo5, evaluationMethod, BigDecimal.valueOf(87.79d),
                BigDecimal.valueOf(0.79d), BigDecimal.valueOf(0.04d));
        EvaluationResultsInfo evaluationResultsInfo6 = createEvaluationResultsInfo(anotherInstancesInfo,
                classifierOptionsInfo6, evaluationMethod, BigDecimal.valueOf(56.80d),
                BigDecimal.valueOf(0.88d), BigDecimal.valueOf(0.09d));
        EvaluationResultsInfo evaluationResultsInfo7 = createEvaluationResultsInfo(instancesInfo,
                classifierOptionsInfo7, evaluationMethod, BigDecimal.valueOf(87.79d),
                BigDecimal.valueOf(0.81d), BigDecimal.valueOf(0.03d));
        evaluationResultsInfoRepository.saveAll(
                Arrays.asList(evaluationResultsInfo1, evaluationResultsInfo2, evaluationResultsInfo3,
                        evaluationResultsInfo4, evaluationResultsInfo5, evaluationResultsInfo6,
                        evaluationResultsInfo7));

        List<ClassifierOptionsInfo> classifierOptionsInfoList =
                classifierOptionsService.findBestClassifierOptions(request);
        assertThat(classifierOptionsInfoList).isNotEmpty();
        assertThat(classifierOptionsInfoList.size()).isEqualTo(ersConfig.getResultSize());
        assertThat(classifierOptionsInfoList.get(0).getClassifierName()).isEqualTo
                (classifierOptionsInfo7.getClassifierName());
        assertThat(classifierOptionsInfoList.get(1).getClassifierName()).isEqualTo
                (classifierOptionsInfo5.getClassifierName());
        assertThat(classifierOptionsInfoList.get(2).getClassifierName()).isEqualTo
                (classifierOptionsInfo4.getClassifierName());
    }
}
