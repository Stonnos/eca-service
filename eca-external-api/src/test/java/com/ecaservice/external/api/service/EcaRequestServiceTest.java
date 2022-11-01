package com.ecaservice.external.api.service;

import com.ecaservice.external.api.AbstractJpaTest;
import com.ecaservice.external.api.entity.EcaRequestEntity;
import com.ecaservice.external.api.entity.RequestStageType;
import com.ecaservice.external.api.exception.ProcessFileException;
import com.ecaservice.external.api.mapping.EcaRequestMapperImpl;
import com.ecaservice.external.api.repository.EvaluationRequestRepository;
import com.ecaservice.external.api.repository.ExperimentRequestRepository;
import com.ecaservice.s3.client.minio.service.ObjectStorageService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

import javax.inject.Inject;
import java.util.UUID;

import static com.ecaservice.external.api.TestHelperUtils.createEvaluationRequestDto;
import static com.ecaservice.external.api.TestHelperUtils.createEvaluationRequestEntity;
import static com.ecaservice.external.api.TestHelperUtils.createExperimentRequestDto;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;

/**
 * Unit tests for {@link EcaRequestService} class.
 *
 * @author Roman Batygin
 */
@Import({EcaRequestService.class, EcaRequestMapperImpl.class})
class EcaRequestServiceTest extends AbstractJpaTest {

    private static final String CLASSIFIER_ABSOLUTE_PATH = "classifier.model";

    @MockBean
    private ObjectStorageService objectStorageService;

    @Inject
    private EvaluationRequestRepository evaluationRequestRepository;
    @Inject
    private ExperimentRequestRepository experimentRequestRepository;

    @Inject
    private EcaRequestService ecaRequestService;

    @Test
    void testSaveEvaluationRequestEntity() {
        var evaluationRequestDto = createEvaluationRequestDto();
        var ecaRequestEntity = ecaRequestService.createAndSaveEvaluationRequestEntity(evaluationRequestDto);
        assertThat(ecaRequestEntity).isNotNull();
        var actual = evaluationRequestRepository.findById(ecaRequestEntity.getId()).orElse(null);
        assertCreatedRequestEntityBaseFields(actual);
        assertThat(actual.isUseOptimalClassifierOptions()).isFalse();
    }

    @Test
    void testSaveEvaluationRequestEntityWithUseOptimalClassifierOptionsFlag() {
        var ecaRequestEntity = ecaRequestService.createAndSaveEvaluationOptimizerRequestEntity();
        assertThat(ecaRequestEntity).isNotNull();
        var actual = evaluationRequestRepository.findById(ecaRequestEntity.getId()).orElse(null);
        assertCreatedRequestEntityBaseFields(actual);
        assertThat(actual.isUseOptimalClassifierOptions()).isTrue();
    }

    @Test
    void testSaveExperimentRequestEntity() {
        var experimentRequestDto = createExperimentRequestDto();
        var ecaRequestEntity = ecaRequestService.createAndSaveExperimentRequestEntity(experimentRequestDto);
        assertThat(ecaRequestEntity).isNotNull();
        var actual = experimentRequestRepository.findById(ecaRequestEntity.getId()).orElse(null);
        assertCreatedRequestEntityBaseFields(actual);
        assertThat(actual.getExperimentType()).isNotNull();
        assertThat(actual.getEvaluationMethod()).isEqualTo(experimentRequestDto.getEvaluationMethod());
    }

    @Test
    void testDeleteClassifierModelSuccess() {
        var evaluationRequestEntity = createEvaluationRequestEntity(UUID.randomUUID().toString());
        evaluationRequestEntity.setClassifierPath(CLASSIFIER_ABSOLUTE_PATH);
        evaluationRequestRepository.save(evaluationRequestEntity);
        ecaRequestService.deleteClassifierModel(evaluationRequestEntity);
        var actual = evaluationRequestRepository.findById(evaluationRequestEntity.getId()).orElse(null);
        assertThat(actual).isNotNull();
        assertThat(actual.getClassifierPath()).isNull();
        assertThat(actual.getDeletedDate()).isNotNull();
    }

    @Test
    void testDeleteClassifierModelWithError() {
        var evaluationRequestEntity = createEvaluationRequestEntity(UUID.randomUUID().toString());
        evaluationRequestEntity.setClassifierPath(CLASSIFIER_ABSOLUTE_PATH);
        evaluationRequestRepository.save(evaluationRequestEntity);
        doThrow(ProcessFileException.class).when(objectStorageService).removeObject(CLASSIFIER_ABSOLUTE_PATH);
        assertThrows(ProcessFileException.class,
                () -> ecaRequestService.deleteClassifierModel(evaluationRequestEntity));
        var actual = evaluationRequestRepository.findById(evaluationRequestEntity.getId()).orElse(null);
        assertThat(actual).isNotNull();
        assertThat(actual.getClassifierPath()).isNotNull();
        assertThat(actual.getDeletedDate()).isNull();
    }

    private void assertCreatedRequestEntityBaseFields(EcaRequestEntity ecaRequestEntity) {
        assertThat(ecaRequestEntity).isNotNull();
        assertThat(ecaRequestEntity).isNotNull();
        assertThat(ecaRequestEntity.getRequestStage()).isEqualTo(RequestStageType.READY);
        assertThat(ecaRequestEntity.getCreationDate()).isNotNull();
        assertThat(ecaRequestEntity.getCorrelationId()).isNotNull();
    }
}
