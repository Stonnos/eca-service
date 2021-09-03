package com.ecaservice.external.api.scheduler;

import com.ecaservice.external.api.AbstractJpaTest;
import com.ecaservice.external.api.config.ExternalApiConfig;
import com.ecaservice.external.api.entity.EcaRequestEntity;
import com.ecaservice.external.api.entity.EvaluationRequestEntity;
import com.ecaservice.external.api.entity.InstancesEntity;
import com.ecaservice.external.api.entity.RequestStageType;
import com.ecaservice.external.api.repository.EcaRequestRepository;
import com.ecaservice.external.api.repository.InstancesRepository;
import com.ecaservice.external.api.service.EcaRequestService;
import com.ecaservice.external.api.service.InstancesService;
import com.ecaservice.external.api.service.RequestStageHandler;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

import javax.inject.Inject;
import java.time.LocalDateTime;

import static com.ecaservice.external.api.TestHelperUtils.createEvaluationRequestEntity;
import static com.ecaservice.external.api.TestHelperUtils.createInstancesEntity;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
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
    @MockBean
    private EcaRequestService ecaRequestService;
    @MockBean
    private InstancesService instancesService;

    @Inject
    private ExternalApiConfig externalApiConfig;
    @Inject
    private InstancesRepository instancesRepository;
    @Inject
    private EcaRequestRepository ecaRequestRepository;

    @Inject
    private EvaluationRequestScheduler evaluationRequestScheduler;

    @Override
    public void deleteAll() {
        instancesRepository.deleteAll();
        ecaRequestRepository.deleteAll();
    }

    @Test
    void testProcessExceededRequests() {
        LocalDateTime dateTime =
                LocalDateTime.now().minusMinutes(externalApiConfig.getEvaluationRequestTimeoutMinutes() + 1);
        ecaRequestRepository.save(createEvaluationRequestEntity(RequestStageType.REQUEST_SENT, null, dateTime));
        ecaRequestRepository.save(
                createEvaluationRequestEntity(RequestStageType.REQUEST_SENT, null, LocalDateTime.now()));
        ecaRequestRepository.save(
                createEvaluationRequestEntity(RequestStageType.ERROR, LocalDateTime.now(), LocalDateTime.now()));
        ecaRequestRepository.save(
                createEvaluationRequestEntity(RequestStageType.COMPLETED, LocalDateTime.now(), dateTime));
        ecaRequestRepository.save(
                createEvaluationRequestEntity(RequestStageType.EXCEEDED, LocalDateTime.now(), LocalDateTime.now()));
        evaluationRequestScheduler.processExceededRequests();
        verify(requestStageHandler, atLeastOnce()).handleExceeded(any(EcaRequestEntity.class));
    }

    @Test
    void testClearClassifiers() {
        LocalDateTime dateTime = LocalDateTime.now().minusDays(externalApiConfig.getNumberOfDaysForStorage() + 1);
        ecaRequestRepository.save(
                createEvaluationRequestEntity(RequestStageType.REQUEST_SENT, null, LocalDateTime.now()));
        ecaRequestRepository.save(
                createEvaluationRequestEntity(RequestStageType.ERROR, LocalDateTime.now(), LocalDateTime.now()));
        ecaRequestRepository.save(createEvaluationRequestEntity(RequestStageType.ERROR, dateTime, LocalDateTime.now()));
        ecaRequestRepository.save(
                createEvaluationRequestEntity(RequestStageType.COMPLETED, dateTime, LocalDateTime.now()));
        ecaRequestRepository.save(
                createEvaluationRequestEntity(RequestStageType.COMPLETED, LocalDateTime.now(), LocalDateTime.now()));
        ecaRequestRepository.save(
                createEvaluationRequestEntity(RequestStageType.EXCEEDED, dateTime, LocalDateTime.now()));
        evaluationRequestScheduler.clearClassifiers();
        verify(ecaRequestService, atLeastOnce()).deleteClassifierModel(any(EvaluationRequestEntity.class));
    }

    @Test
    void testClearData() {
        LocalDateTime dateTime = LocalDateTime.now().minusDays(externalApiConfig.getNumberOfDaysForStorage() + 1);
        instancesRepository.save(createInstancesEntity(LocalDateTime.now()));
        instancesRepository.save(createInstancesEntity(dateTime));
        evaluationRequestScheduler.clearData();
        verify(instancesService, atLeastOnce()).deleteInstances(any(InstancesEntity.class));
    }
}
