package com.ecaservice.core.redelivery.service;

import com.ecaservice.core.redelivery.AbstractJpaTest;
import com.ecaservice.core.redelivery.config.RedeliveryProperties;
import com.ecaservice.core.redelivery.entity.RetryRequest;
import com.ecaservice.core.redelivery.repository.RetryRequestRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.stream.IntStream;

import static com.ecaservice.core.redelivery.TestHelperUtils.createRetryRequest;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for {@link RequestRedeliveryService} class.
 *
 * @author Roman Batygin
 */
@Import({RequestRedeliveryService.class, RedeliveryProperties.class})
class RequestRedeliveryServiceTest extends AbstractJpaTest {

    private static final int REQUESTS_SIZE = 10;
    private static final long SECONDS = 60000L;

    @MockBean
    private RetryService retryService;

    @Inject
    private RetryRequestRepository retryRequestRepository;
    @Inject
    private RequestRedeliveryService requestRedeliveryService;

    @Override
    public void deleteAll() {
        retryRequestRepository.deleteAll();
    }

    @Test
    void testProcessRequests() {
        IntStream.range(0, REQUESTS_SIZE).forEach(i -> createAndSaveRetryRequest());
        var futureRetryRequest = createRetryRequest();
        futureRetryRequest.setRetryAt(LocalDateTime.now().plusSeconds(SECONDS));
        retryRequestRepository.save(futureRetryRequest);
        requestRedeliveryService.processNotSentRequests();
        verify(retryService, times(REQUESTS_SIZE)).retry(any(RetryRequest.class));
    }

    private void createAndSaveRetryRequest() {
        var retryRequest = createRetryRequest();
        retryRequestRepository.save(retryRequest);
    }
}
