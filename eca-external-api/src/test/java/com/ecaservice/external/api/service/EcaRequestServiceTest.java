package com.ecaservice.external.api.service;

import com.ecaservice.external.api.AbstractJpaTest;
import com.ecaservice.external.api.dto.EvaluationRequestDto;
import com.ecaservice.external.api.entity.EcaRequestEntity;
import com.ecaservice.external.api.entity.EvaluationRequestEntity;
import com.ecaservice.external.api.entity.RequestStageType;
import com.ecaservice.external.api.exception.ProcessFileException;
import com.ecaservice.external.api.mapping.EcaRequestMapperImpl;
import com.ecaservice.external.api.repository.EvaluationRequestRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

import javax.inject.Inject;
import java.util.UUID;

import static com.ecaservice.external.api.TestHelperUtils.createEvaluationRequestDto;
import static com.ecaservice.external.api.TestHelperUtils.createEvaluationRequestEntity;
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

    private static final String CLASSIFIER_ABSOLUTE_PATH = "/home/roman/classifier.model";

    @MockBean
    private FileDataService fileDataService;

    @Inject
    private EvaluationRequestRepository evaluationRequestRepository;

    @Inject
    private EcaRequestService ecaRequestService;

    @Test
    void testSaveRequestEntity() {
        EvaluationRequestDto evaluationRequestDto = createEvaluationRequestDto();
        EcaRequestEntity ecaRequestEntity = ecaRequestService.createAndSaveRequestEntity(evaluationRequestDto);
        assertThat(ecaRequestEntity).isNotNull();
        EvaluationRequestEntity actual = evaluationRequestRepository.findById(ecaRequestEntity.getId()).orElse(null);
        assertThat(actual).isNotNull();
        assertThat(actual.getRequestStage()).isEqualTo(RequestStageType.READY);
        assertThat(actual.getCreationDate()).isNotNull();
        assertThat(actual.getCorrelationId()).isNotNull();
    }

    @Test
    void testDeleteClassifierModelSuccess() {
        var evaluationRequestEntity = createEvaluationRequestEntity(UUID.randomUUID().toString());
        evaluationRequestEntity.setClassifierAbsolutePath(CLASSIFIER_ABSOLUTE_PATH);
        evaluationRequestRepository.save(evaluationRequestEntity);
        ecaRequestService.deleteClassifierModel(evaluationRequestEntity);
        var actual = evaluationRequestRepository.findById(evaluationRequestEntity.getId()).orElse(null);
        assertThat(actual).isNotNull();
        assertThat(actual.getClassifierAbsolutePath()).isNull();
        assertThat(actual.getDeletedDate()).isNotNull();
    }

    @Test
    void testDeleteClassifierModelWithError() {
        var evaluationRequestEntity = createEvaluationRequestEntity(UUID.randomUUID().toString());
        evaluationRequestEntity.setClassifierAbsolutePath(CLASSIFIER_ABSOLUTE_PATH);
        evaluationRequestRepository.save(evaluationRequestEntity);
        doThrow(ProcessFileException.class).when(fileDataService).delete(CLASSIFIER_ABSOLUTE_PATH);
        assertThrows(ProcessFileException.class,
                () -> ecaRequestService.deleteClassifierModel(evaluationRequestEntity));
        var actual = evaluationRequestRepository.findById(evaluationRequestEntity.getId()).orElse(null);
        assertThat(actual).isNotNull();
        assertThat(actual.getClassifierAbsolutePath()).isNotNull();
        assertThat(actual.getDeletedDate()).isNull();
    }
}
