package com.ecaservice.external.api.service;

import com.ecaservice.base.model.EvaluationResponse;
import com.ecaservice.classifier.options.config.ClassifiersOptionsAutoConfiguration;
import com.ecaservice.external.api.AbstractJpaTest;
import com.ecaservice.external.api.config.ExternalApiConfig;
import com.ecaservice.external.api.entity.EvaluationRequestEntity;
import com.ecaservice.external.api.entity.RequestStageType;
import com.ecaservice.external.api.repository.EvaluationRequestRepository;
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

/**
 * Unit tests for checking {@link EcaResponseHandler} functionality.
 *
 * @author Roman Batygin
 */
@Import({EcaResponseHandler.class, ClassifiersOptionsAutoConfiguration.class})
class EcaResponseHandlerTest extends AbstractJpaTest {

    private static final String CLASSIFIER_MODEL_PATH_FORMAT = "classifier-%s.model";

    @MockBean
    private ObjectStorageService objectStorageService;

    @Inject
    private ExternalApiConfig externalApiConfig;

    @Inject
    private EvaluationRequestRepository evaluationRequestRepository;

    @Inject
    private EcaResponseHandler ecaResponseHandler;

    @Override
    public void deleteAll() {
        evaluationRequestRepository.deleteAll();
    }

    @Test
    void testSuccessResponseHandle() {
        EvaluationRequestEntity evaluationRequestEntity =
                createEvaluationRequestEntity(RequestStageType.RESPONSE_RECEIVED, null, LocalDateTime.now());
        evaluationRequestEntity.setUseOptimalClassifierOptions(true);
        evaluationRequestRepository.save(evaluationRequestEntity);
        EvaluationResponse evaluationResponse = successEvaluationResponse();
        String expectedClassifierPath =
                String.format(CLASSIFIER_MODEL_PATH_FORMAT, evaluationRequestEntity.getCorrelationId());
        ecaResponseHandler.handleResponse(evaluationRequestEntity, evaluationResponse);
        var actual =
                internalTestResponseHandle(evaluationRequestEntity, evaluationResponse, RequestStageType.COMPLETED);
        assertThat(actual.getClassifierAbsolutePath()).isEqualTo(expectedClassifierPath);
        assertThat(actual.getClassifierOptionsJson()).isNotNull();
    }

    @Test
    void testResponseHandleWithErrorTechnicalStatus() {
        EvaluationRequestEntity evaluationRequestEntity =
                createEvaluationRequestEntity(RequestStageType.RESPONSE_RECEIVED, null, LocalDateTime.now());
        evaluationRequestRepository.save(evaluationRequestEntity);
        EvaluationResponse evaluationResponse = errorEvaluationResponse();
        internalTestResponseHandle(evaluationRequestEntity, evaluationResponse, RequestStageType.ERROR);
    }

    @Test
    void testResponseHandleWithError() throws Exception {
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
        ecaResponseHandler.handleResponse(evaluationRequestEntity, evaluationResponse);
        EvaluationRequestEntity actual =
                evaluationRequestRepository.findById(evaluationRequestEntity.getId()).orElse(null);
        assertThat(actual).isNotNull();
        assertThat(actual.getEndDate()).isNotNull();
        assertThat(actual.getRequestStage()).isEqualTo(expected);
        return actual;
    }
}
