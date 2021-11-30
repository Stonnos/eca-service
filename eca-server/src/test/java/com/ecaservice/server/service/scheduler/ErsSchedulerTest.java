package com.ecaservice.server.service.scheduler;

import com.ecaservice.ers.dto.EvaluationResultsRequest;
import com.ecaservice.server.config.ers.ErsConfig;
import com.ecaservice.server.mapping.ErsResponseStatusMapperImpl;
import com.ecaservice.server.model.entity.ErsRequest;
import com.ecaservice.server.model.entity.ErsResponseStatus;
import com.ecaservice.server.model.entity.ErsRetryRequest;
import com.ecaservice.server.model.entity.EvaluationLog;
import com.ecaservice.server.model.entity.EvaluationResultsRequestEntity;
import com.ecaservice.server.repository.ErsRequestRepository;
import com.ecaservice.server.repository.ErsRetryRequestRepository;
import com.ecaservice.server.repository.EvaluationLogRepository;
import com.ecaservice.server.service.AbstractJpaTest;
import com.ecaservice.server.service.ers.ErsErrorHandler;
import com.ecaservice.server.service.ers.ErsRedeliveryService;
import com.ecaservice.server.service.ers.ErsRequestService;
import com.ecaservice.server.service.ers.ErsRetryRequestCacheService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.UUID;

import static com.ecaservice.server.TestHelperUtils.createEvaluationLog;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for class {@link ErsScheduler}
 *
 * @author Roman Batygin
 */
@Import({ErsRedeliveryService.class, ErsScheduler.class, ErsConfig.class, ErsErrorHandler.class,
        ErsRetryRequestCacheService.class, ErsResponseStatusMapperImpl.class})
class ErsSchedulerTest extends AbstractJpaTest {

    @MockBean
    private ErsRequestService ersRequestService;

    @Inject
    private ErsRequestRepository ersRequestRepository;
    @Inject
    private ErsRetryRequestRepository ersRetryRequestRepository;
    @Inject
    private EvaluationLogRepository evaluationLogRepository;

    @Inject
    private ErsRedeliveryService ersRedeliveryService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private ErsScheduler ersScheduler;

    @Override
    public void init() {
        ersScheduler = new ErsScheduler(ersRedeliveryService);
        createAndSaveErsRequests();
    }

    @Override
    public void deleteAll() {
        ersRetryRequestRepository.deleteAll();
        ersRequestRepository.deleteAll();
        evaluationLogRepository.deleteAll();
    }

    @Test
    void testSuccessResend() {
        ersScheduler.resendErsRequests();
        verify(ersRequestService, atLeastOnce()).saveEvaluationResults(any(EvaluationResultsRequest.class),
                any(ErsRequest.class));
    }

    @Test
    void testResendWithError() {
        doThrow(new RuntimeException())
                .when(ersRequestService)
                .saveEvaluationResults(any(EvaluationResultsRequest.class), any(ErsRequest.class));
        ersScheduler.resendErsRequests();
        var ersRequests = ersRequestRepository.findAll();
        assertThat(ersRequests).isNotEmpty();
        var ersRequest = ersRequests.iterator().next();
        assertThat(ersRequest).isNotNull();
        assertThat(ersRequest.getResponseStatus()).isEqualTo(ErsResponseStatus.ERROR);
        assertThat(ersRetryRequestRepository.count()).isZero();
    }

    private void createAndSaveErsRequests() {
        EvaluationLog evaluationLog = createEvaluationLog();
        evaluationLogRepository.save(evaluationLog);
        var ersRequest = createAndSaveErsRequest(evaluationLog);
        createAndSaveErsRetryRequest(ersRequest);
    }

    private ErsRequest createAndSaveErsRequest(EvaluationLog evaluationLog) {
        var evaluationResultsRequestEntity = new EvaluationResultsRequestEntity();
        evaluationResultsRequestEntity.setEvaluationLog(evaluationLog);
        evaluationResultsRequestEntity.setResponseStatus(ErsResponseStatus.SERVICE_UNAVAILABLE);
        evaluationResultsRequestEntity.setRequestId(UUID.randomUUID().toString());
        evaluationResultsRequestEntity.setRequestDate(LocalDateTime.now());
        return ersRequestRepository.save(evaluationResultsRequestEntity);
    }

    @SneakyThrows
    private void createAndSaveErsRetryRequest(ErsRequest ersRequest) {
        var ersRetryRequest = new ErsRetryRequest();
        ersRetryRequest.setErsRequest(ersRequest);
        ersRetryRequest.setJsonRequest(objectMapper.writeValueAsString(new EvaluationResultsRequest()));
        ersRetryRequest.setCreated(LocalDateTime.now());
        ersRetryRequestRepository.save(ersRetryRequest);
    }
}
