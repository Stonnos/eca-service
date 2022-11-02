package com.ecaservice.external.api.scheduler;

import com.ecaservice.external.api.AbstractJpaTest;
import com.ecaservice.external.api.config.ExternalApiConfig;
import com.ecaservice.external.api.entity.EcaRequestEntity;
import com.ecaservice.external.api.entity.RequestStageType;
import com.ecaservice.external.api.repository.EcaRequestRepository;
import com.ecaservice.external.api.service.RequestStageHandler;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

import javax.inject.Inject;
import java.time.LocalDateTime;

import static com.ecaservice.external.api.TestHelperUtils.createEvaluationRequestEntity;
import static com.ecaservice.external.api.TestHelperUtils.createExperimentRequestEntity;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for checking {@link EvaluationRequestScheduler} functionality.
 *
 * @author Roman Batygin
 */
@Import({ExternalApiConfig.class, EvaluationRequestScheduler.class})
class EvaluationRequestSchedulerTest extends AbstractJpaTest {

    @MockBean
    private RequestStageHandler requestStageHandler;

    @Inject
    private ExternalApiConfig externalApiConfig;
    @Inject
    private EcaRequestRepository ecaRequestRepository;

    @Inject
    private EvaluationRequestScheduler evaluationRequestScheduler;

    @Override
    public void deleteAll() {
        ecaRequestRepository.deleteAll();
    }

    @Test
    void testProcessExceededRequests() {
        LocalDateTime dateTime =
                LocalDateTime.now().minusMinutes(externalApiConfig.getEvaluationRequestTimeoutMinutes() + 1);
        ecaRequestRepository.save(createEvaluationRequestEntity(RequestStageType.REQUEST_SENT, null, dateTime));
        ecaRequestRepository.save(createExperimentRequestEntity(RequestStageType.REQUEST_SENT,
                LocalDateTime.now().minusMinutes(externalApiConfig.getExperimentRequestTimeoutMinutes() + 1)));
        ecaRequestRepository.save(createEvaluationRequestEntity(RequestStageType.REQUEST_SENT, null,
                LocalDateTime.now().plusMinutes(1L)));
        ecaRequestRepository.save(
                createEvaluationRequestEntity(RequestStageType.ERROR, LocalDateTime.now(), LocalDateTime.now()));
        ecaRequestRepository.save(
                createEvaluationRequestEntity(RequestStageType.COMPLETED, LocalDateTime.now(), dateTime));
        ecaRequestRepository.save(
                createEvaluationRequestEntity(RequestStageType.EXCEEDED, LocalDateTime.now(), LocalDateTime.now()));
        evaluationRequestScheduler.processExceededRequests();
        verify(requestStageHandler, times(2)).handleExceeded(any(EcaRequestEntity.class));
    }
}
