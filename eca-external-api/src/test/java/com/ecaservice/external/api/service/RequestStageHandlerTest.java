package com.ecaservice.external.api.service;

import com.ecaservice.external.api.AbstractJpaTest;
import com.ecaservice.external.api.config.ExternalApiConfig;
import com.ecaservice.external.api.entity.EvaluationRequestEntity;
import com.ecaservice.external.api.entity.RequestStageType;
import com.ecaservice.external.api.repository.EvaluationRequestRepository;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;

import javax.inject.Inject;

import static com.ecaservice.external.api.TestHelperUtils.createEvaluationRequestEntity;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link RequestStageHandler} functionality.
 *
 * @author Roman Batygin
 */
@Import({RequestStageHandler.class, ExternalApiConfig.class})
class RequestStageHandlerTest extends AbstractJpaTest {

    private static final String ERROR_MESSAGE = "Error";

    @Inject
    private EvaluationRequestRepository evaluationRequestRepository;
    @Inject
    private RequestStageHandler requestStageHandler;

    @Override
    public void deleteAll() {
        evaluationRequestRepository.deleteAll();
    }

    @Test
    void testHandleError() {
        EvaluationRequestEntity evaluationRequestEntity =
                createEvaluationRequestEntity(RequestStageType.READY, null);
        evaluationRequestRepository.save(evaluationRequestEntity);
        requestStageHandler.handleError(evaluationRequestEntity, ERROR_MESSAGE);
        assertRequestStage(evaluationRequestEntity.getId(), RequestStageType.ERROR);
    }

    @Test
    void testHandleExceeded() {
        EvaluationRequestEntity evaluationRequestEntity =
                createEvaluationRequestEntity(RequestStageType.READY, null);
        evaluationRequestRepository.save(evaluationRequestEntity);
        requestStageHandler.handleExceeded(evaluationRequestEntity);
        assertRequestStage(evaluationRequestEntity.getId(), RequestStageType.EXCEEDED);
    }

    private void assertRequestStage(long id, RequestStageType expected) {
        EvaluationRequestEntity actual = evaluationRequestRepository.findById(id).orElse(null);
        assertThat(actual).isNotNull();
        assertThat(actual.getRequestStage()).isEqualTo(expected);
    }
}
