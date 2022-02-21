package com.ecaservice.server.service.ers;

import com.ecaservice.core.redelivery.model.RetryContext;
import com.ecaservice.server.TestHelperUtils;
import com.ecaservice.server.mapping.ErsResponseStatusMapperImpl;
import com.ecaservice.server.model.entity.ErsRequest;
import com.ecaservice.server.model.entity.ErsResponseStatus;
import com.ecaservice.server.model.entity.EvaluationResultsRequestEntity;
import com.ecaservice.server.repository.ErsRequestRepository;
import com.ecaservice.server.repository.EvaluationLogRepository;
import com.ecaservice.server.service.AbstractJpaTest;
import feign.FeignException;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;

import javax.inject.Inject;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Unit tests for checking {@link ErsRetryCallback} functionality.
 *
 * @author Roman Batygin
 */
@Import({ErsRetryCallback.class, ErsErrorHandler.class, ErsResponseStatusMapperImpl.class})
class ErsRetryCallbackTest extends AbstractJpaTest {

    private static final int MAX_RETRIES = 1;

    @Inject
    private EvaluationLogRepository evaluationLogRepository;
    @Inject
    private ErsRequestRepository ersRequestRepository;
    @Inject
    private ErsRetryCallback ersRetryCallback;

    private ErsRequest ersRequest;

    @Override
    public void init() {
        ersRequest = createAndSaveErsRequest();
    }

    @Override
    public void deleteAll() {
        ersRequestRepository.deleteAll();
        evaluationLogRepository.deleteAll();
    }

    @Test
    void testSuccessCallback() {
        ersRetryCallback.onSuccess(new RetryContext(ersRequest.getRequestId(), 0, MAX_RETRIES));
        internalVerifyResponseStatus(ErsResponseStatus.SUCCESS);
    }

    @Test
    void testErrorCallbackWithServiceUnavailable() {
        FeignException.ServiceUnavailable serviceUnavailable = mock(FeignException.ServiceUnavailable.class);
        ersRetryCallback.onError(
                new RetryContext(ersRequest.getRequestId(), 0, MAX_RETRIES), serviceUnavailable);
        internalVerifyResponseStatus(ErsResponseStatus.SERVICE_UNAVAILABLE);
    }

    @Test
    void testRetryExhaustedCallback() {
        ersRetryCallback.onRetryExhausted(new RetryContext(ersRequest.getRequestId(), 0, MAX_RETRIES));
        internalVerifyResponseStatus(ErsResponseStatus.ERROR);
    }

    private ErsRequest createAndSaveErsRequest() {
        var evaluationLog = TestHelperUtils.createEvaluationLog();
        evaluationLogRepository.save(evaluationLog);
        var requestEntity = new EvaluationResultsRequestEntity();
        requestEntity.setRequestId(UUID.randomUUID().toString());
        requestEntity.setResponseStatus(ErsResponseStatus.SERVICE_UNAVAILABLE);
        requestEntity.setEvaluationLog(evaluationLog);
        return ersRequestRepository.save(requestEntity);
    }

    private void internalVerifyResponseStatus(ErsResponseStatus expectedStatus) {
        var actual = ersRequestRepository.findById(ersRequest.getId()).orElse(null);
        assertThat(actual).isNotNull();
        assertThat(actual.getResponseStatus()).isEqualTo(expectedStatus);
    }
}
