package com.ecaservice.external.api.service;

import com.ecaservice.external.api.AbstractJpaTest;
import com.ecaservice.external.api.dto.EvaluationStatus;
import com.ecaservice.external.api.entity.RequestStageType;
import com.ecaservice.external.api.mapping.EcaRequestMapperImpl;
import com.ecaservice.external.api.mapping.EvaluationStatusMapperImpl;
import com.ecaservice.external.api.repository.EvaluationRequestRepository;
import com.ecaservice.s3.client.minio.service.ObjectStorageService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;
import java.time.LocalDateTime;

import static com.ecaservice.external.api.TestHelperUtils.createEvaluationRequestEntity;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link EvaluationResponseService} functionality.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
@Import({EvaluationResponseService.class, EvaluationStatusMapperImpl.class,
        EcaRequestService.class, EcaRequestMapperImpl.class})
class EvaluationResponseServiceTest extends AbstractJpaTest {

    private static final String CLASSIFIER_DOWNLOAD_URL = "http://localhost:9000/object-storage";

    @MockBean
    private ObjectStorageService objectStorageService;

    @Inject
    private EvaluationRequestRepository evaluationRequestRepository;
    @Inject
    private EvaluationResponseService evaluationResponseService;

    @Override
    public void deleteAll() {
        evaluationRequestRepository.deleteAll();
    }

    @Test
    void testBuildResponseWithError() {
        var evaluationRequestEntity =
                createEvaluationRequestEntity(RequestStageType.ERROR, LocalDateTime.now(), LocalDateTime.now());
        evaluationRequestRepository.save(evaluationRequestEntity);
        var evaluationResponseDto =
                evaluationResponseService.processEvaluationResultsResponse(evaluationRequestEntity.getCorrelationId());
        assertThat(evaluationResponseDto).isNotNull();
        assertThat(evaluationResponseDto.getRequestId()).isEqualTo(
                evaluationRequestEntity.getCorrelationId());
        assertThat(evaluationResponseDto.getEvaluationStatus()).isEqualTo(EvaluationStatus.ERROR);
    }

    @Test
    void testBuildSuccessResponse() {
        var evaluationRequestEntity =
                createEvaluationRequestEntity(RequestStageType.COMPLETED, LocalDateTime.now(), LocalDateTime.now());
        evaluationRequestEntity.setClassifierDownloadUrl(CLASSIFIER_DOWNLOAD_URL);
        evaluationRequestRepository.save(evaluationRequestEntity);
        var evaluationResponseDto =
                evaluationResponseService.processEvaluationResultsResponse(evaluationRequestEntity.getCorrelationId());
        assertThat(evaluationResponseDto).isNotNull();
        assertThat(evaluationResponseDto.getEvaluationStatus()).isEqualTo(EvaluationStatus.FINISHED);
        assertThat(evaluationResponseDto.getRequestId()).isEqualTo(
                evaluationRequestEntity.getCorrelationId());
        assertThat(evaluationResponseDto.getModelUrl()).isEqualTo(CLASSIFIER_DOWNLOAD_URL);
    }
}
