package com.ecaservice.external.api.service;

import com.ecaservice.base.model.EvaluationResponse;
import com.ecaservice.classifier.options.config.ClassifiersOptionsAutoConfiguration;
import com.ecaservice.external.api.AbstractJpaTest;
import com.ecaservice.external.api.config.ExternalApiConfig;
import com.ecaservice.external.api.entity.EvaluationRequestEntity;
import com.ecaservice.external.api.entity.RequestStageType;
import com.ecaservice.external.api.mapping.EcaRequestMapperImpl;
import com.ecaservice.external.api.repository.EvaluationRequestRepository;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;

import javax.inject.Inject;
import java.time.LocalDateTime;

import static com.ecaservice.external.api.TestHelperUtils.createEvaluationRequestEntity;
import static com.ecaservice.external.api.TestHelperUtils.errorEvaluationResponse;
import static com.ecaservice.external.api.TestHelperUtils.successEvaluationResponse;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for checking {@link EvaluationResponseHandler} functionality.
 *
 * @author Roman Batygin
 */
@Import({EvaluationResponseHandler.class, ClassifiersOptionsAutoConfiguration.class, ExternalApiConfig.class,
        EcaRequestMapperImpl.class, RequestStageHandler.class})
class EvaluationResponseHandlerTest extends AbstractJpaTest {

    @Inject
    private EvaluationRequestRepository evaluationRequestRepository;

    @Inject
    private EvaluationResponseHandler evaluationResponseHandler;

    @Override
    public void deleteAll() {
        evaluationRequestRepository.deleteAll();
    }

    @Test
    void testSuccessEvaluationResponseHandle() {
        EvaluationRequestEntity evaluationRequestEntity =
                createEvaluationRequestEntity(RequestStageType.REQUEST_SENT, null, LocalDateTime.now());
        evaluationRequestEntity.setUseOptimalClassifierOptions(true);
        evaluationRequestRepository.save(evaluationRequestEntity);
        EvaluationResponse evaluationResponse = successEvaluationResponse();
        evaluationResponseHandler.handleResponse(evaluationRequestEntity, evaluationResponse);
        var actual =
                internalTestResponseHandle(evaluationRequestEntity, evaluationResponse, RequestStageType.COMPLETED);
        assertThat(actual.getClassifierDownloadUrl()).isEqualTo(evaluationResponse.getModelUrl());
    }

    @Test
    void testEvaluationResponseHandleWithErrorTechnicalStatus() {
        EvaluationRequestEntity evaluationRequestEntity =
                createEvaluationRequestEntity(RequestStageType.REQUEST_SENT, null, LocalDateTime.now());
        evaluationRequestRepository.save(evaluationRequestEntity);
        EvaluationResponse evaluationResponse = errorEvaluationResponse();
        internalTestResponseHandle(evaluationRequestEntity, evaluationResponse, RequestStageType.ERROR);
    }

    private EvaluationRequestEntity internalTestResponseHandle(EvaluationRequestEntity evaluationRequestEntity,
                                                               EvaluationResponse evaluationResponse,
                                                               RequestStageType expected) {
        evaluationResponseHandler.handleResponse(evaluationRequestEntity, evaluationResponse);
        EvaluationRequestEntity actual =
                evaluationRequestRepository.findById(evaluationRequestEntity.getId()).orElse(null);
        assertThat(actual).isNotNull();
        assertThat(actual.getEndDate()).isNotNull();
        assertThat(actual.getRequestStage()).isEqualTo(expected);
        return actual;
    }
}
