package com.ecaservice.external.api.service;

import com.ecaservice.base.model.EvaluationResponse;
import com.ecaservice.classifier.options.config.ClassifiersOptionsAutoConfiguration;
import com.ecaservice.external.api.AbstractJpaTest;
import com.ecaservice.external.api.config.ExternalApiConfig;
import com.ecaservice.external.api.entity.EvaluationRequestEntity;
import com.ecaservice.external.api.entity.RequestStageType;
import com.ecaservice.external.api.repository.EvaluationRequestRepository;
import com.ecaservice.s3.client.minio.model.GetPresignedUrlObject;
import com.ecaservice.s3.client.minio.service.ObjectStorageService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

import javax.inject.Inject;
import java.time.LocalDateTime;

import static com.ecaservice.external.api.TestHelperUtils.createEvaluationRequestEntity;
import static com.ecaservice.external.api.TestHelperUtils.errorEvaluationResponse;
import static com.ecaservice.external.api.TestHelperUtils.successEvaluationResponse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

/**
 * Unit tests for checking {@link EcaResponseHandler} functionality.
 *
 * @author Roman Batygin
 */
@Import({EcaResponseHandler.class, ClassifiersOptionsAutoConfiguration.class, ExternalApiConfig.class})
class EcaResponseHandlerTest extends AbstractJpaTest {

    private static final String CLASSIFIER_MODEL_PATH_FORMAT = "classifier-%s.model";
    private static final String CLASSIFIER_DOWNLOAD_URL = "http://localhost:9000/object-storage";

    @MockBean
    private ObjectStorageService objectStorageService;

    @Inject
    private EvaluationRequestRepository evaluationRequestRepository;

    @Inject
    private EcaResponseHandler ecaResponseHandler;

    @Override
    public void deleteAll() {
        evaluationRequestRepository.deleteAll();
    }

    @Test
    void testSuccessEvaluationResponseHandle() {
        EvaluationRequestEntity evaluationRequestEntity =
                createEvaluationRequestEntity(RequestStageType.RESPONSE_RECEIVED, null, LocalDateTime.now());
        evaluationRequestEntity.setUseOptimalClassifierOptions(true);
        evaluationRequestRepository.save(evaluationRequestEntity);
        EvaluationResponse evaluationResponse = successEvaluationResponse();
        String expectedClassifierPath =
                String.format(CLASSIFIER_MODEL_PATH_FORMAT, evaluationRequestEntity.getCorrelationId());
        when(objectStorageService.getObjectPresignedProxyUrl(any(GetPresignedUrlObject.class)))
                .thenReturn(CLASSIFIER_DOWNLOAD_URL);
        ecaResponseHandler.handleEvaluationResponse(evaluationRequestEntity, evaluationResponse);
        var actual =
                internalTestResponseHandle(evaluationRequestEntity, evaluationResponse, RequestStageType.COMPLETED);
        assertThat(actual.getClassifierPath()).isEqualTo(expectedClassifierPath);
        assertThat(actual.getClassifierOptionsJson()).isNotNull();
        assertThat(actual.getClassifierDownloadUrl()).isEqualTo(CLASSIFIER_DOWNLOAD_URL);
    }

    @Test
    void testEvaluationResponseHandleWithErrorTechnicalStatus() {
        EvaluationRequestEntity evaluationRequestEntity =
                createEvaluationRequestEntity(RequestStageType.RESPONSE_RECEIVED, null, LocalDateTime.now());
        evaluationRequestRepository.save(evaluationRequestEntity);
        EvaluationResponse evaluationResponse = errorEvaluationResponse();
        internalTestResponseHandle(evaluationRequestEntity, evaluationResponse, RequestStageType.ERROR);
    }

    @Test
    void testEvaluationResponseHandleWithError() throws Exception {
        EvaluationRequestEntity evaluationRequestEntity =
                createEvaluationRequestEntity(RequestStageType.RESPONSE_RECEIVED, null, LocalDateTime.now());
        evaluationRequestRepository.save(evaluationRequestEntity);
        EvaluationResponse evaluationResponse = successEvaluationResponse();
        doThrow(IllegalStateException.class).when(objectStorageService).uploadObject(any(), anyString());
        internalTestResponseHandle(evaluationRequestEntity, evaluationResponse, RequestStageType.ERROR);
    }

    private EvaluationRequestEntity internalTestResponseHandle(EvaluationRequestEntity evaluationRequestEntity,
                                                               EvaluationResponse evaluationResponse,
                                                               RequestStageType expected) {
        ecaResponseHandler.handleEvaluationResponse(evaluationRequestEntity, evaluationResponse);
        EvaluationRequestEntity actual =
                evaluationRequestRepository.findById(evaluationRequestEntity.getId()).orElse(null);
        assertThat(actual).isNotNull();
        assertThat(actual.getEndDate()).isNotNull();
        assertThat(actual.getRequestStage()).isEqualTo(expected);
        return actual;
    }
}
