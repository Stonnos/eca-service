package com.ecaservice.core.redelivery.service;

import com.ecaservice.core.redelivery.AbstractJpaTest;
import com.ecaservice.core.redelivery.aspect.RetryAspect;
import com.ecaservice.core.redelivery.config.RedeliveryProperties;
import com.ecaservice.core.redelivery.entity.RetryRequest;
import com.ecaservice.core.redelivery.metrics.RetryMeterService;
import com.ecaservice.core.redelivery.repository.RetryRequestRepository;
import com.ecaservice.core.redelivery.test.RedeliverTestConfiguration;
import com.ecaservice.core.redelivery.test.model.TestRequest;
import com.ecaservice.core.redelivery.test.service.TestServiceA;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import static com.ecaservice.core.redelivery.TestHelperUtils.createRetryRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for {@link RequestRedeliveryService} class.
 *
 * @author Roman Batygin
 */
@EnableAspectJAutoProxy
@Import({RequestRedeliveryService.class, RedeliveryProperties.class, RetryService.class,
        RetryRequestCacheService.class, RedeliverTestConfiguration.class, RetryAspect.class, RetryRequestFetcher.class})
class RequestRedeliveryServiceTest extends AbstractJpaTest {

    private static final String REQUEST_1 = "REQUEST_1";
    private static final int REQUESTS_SIZE = 10;
    private static final long SECONDS = 60000L;
    private static final int MAX_RETRIES = 10;

    @MockBean
    private RetryMeterService retryMeterService;

    @SpyBean
    private TestServiceA testServiceA;

    @SpyBean
    private RetryService retryService;

    @Captor
    private ArgumentCaptor<RetryRequest> requestArgumentCaptor;

    @Autowired
    private RetryRequestRepository retryRequestRepository;
    @Autowired
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
        verify(retryService, times(REQUESTS_SIZE)).retry(requestArgumentCaptor.capture());
        var requests = requestArgumentCaptor.getAllValues();
        checkUniqueIds(requests);

    }

    private void checkUniqueIds(List<RetryRequest> retryRequestList) {
        Set<Long> uniqueIds = new HashSet<>();
        for (RetryRequest retryRequest : retryRequestList) {
            assertThat(uniqueIds.add(retryRequest.getId())).isTrue();
        }
    }

    private void createAndSaveRetryRequest() {
        var retryRequest = createRetryRequest(REQUEST_1, new TestRequest(), 0, MAX_RETRIES);
        retryRequestRepository.save(retryRequest);
    }
}
