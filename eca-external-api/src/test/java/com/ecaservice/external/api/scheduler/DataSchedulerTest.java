package com.ecaservice.external.api.scheduler;

import com.ecaservice.external.api.AbstractJpaTest;
import com.ecaservice.external.api.config.ExternalApiConfig;
import com.ecaservice.external.api.entity.RequestStageType;
import com.ecaservice.external.api.repository.EcaRequestRepository;
import com.ecaservice.external.api.repository.InstancesRepository;
import com.ecaservice.external.api.service.FileDataService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

import javax.inject.Inject;
import java.time.LocalDateTime;

import static com.ecaservice.external.api.TestHelperUtils.createEvaluationRequestEntity;
import static com.ecaservice.external.api.TestHelperUtils.createInstancesEntity;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for checking {@link DataScheduler} functionality.
 *
 * @author Roman Batygin
 */
@Import({ExternalApiConfig.class, DataScheduler.class})
class DataSchedulerTest extends AbstractJpaTest {

    @MockBean
    private FileDataService fileDataService;

    @Inject
    private ExternalApiConfig externalApiConfig;
    @Inject
    private InstancesRepository instancesRepository;
    @Inject
    private EcaRequestRepository ecaRequestRepository;

    @Inject
    private DataScheduler dataScheduler;

    @Override
    public void deleteAll() {
        instancesRepository.deleteAll();
        ecaRequestRepository.deleteAll();
    }

    @Test
    void testClearClassifiers() {
        LocalDateTime dateTime = LocalDateTime.now().minusDays(externalApiConfig.getNumberOfDaysForStorage() + 1);
        ecaRequestRepository.save(createEvaluationRequestEntity(RequestStageType.REQUEST_SENT, null));
        ecaRequestRepository.save(createEvaluationRequestEntity(RequestStageType.ERROR, LocalDateTime.now()));
        ecaRequestRepository.save(createEvaluationRequestEntity(RequestStageType.ERROR, dateTime));
        ecaRequestRepository.save(createEvaluationRequestEntity(RequestStageType.COMPLETED, dateTime));
        ecaRequestRepository.save(createEvaluationRequestEntity(RequestStageType.COMPLETED, LocalDateTime.now()));
        ecaRequestRepository.save(createEvaluationRequestEntity(RequestStageType.EXCEEDED, dateTime));
        when(fileDataService.delete(anyString())).thenReturn(true);
        dataScheduler.clearClassifiers();
        verify(fileDataService, atLeastOnce()).delete(any());
    }

    @Test
    void testClearData() {
        LocalDateTime dateTime = LocalDateTime.now().minusDays(externalApiConfig.getNumberOfDaysForStorage() + 1);
        instancesRepository.save(createInstancesEntity(LocalDateTime.now()));
        instancesRepository.save(createInstancesEntity(dateTime));
        when(fileDataService.delete(anyString())).thenReturn(true);
        dataScheduler.clearData();
        verify(fileDataService, atLeastOnce()).delete(anyString());
        assertThat(instancesRepository.count()).isOne();
    }
}
