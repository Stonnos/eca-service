package com.ecaservice.core.redelivery.service;

import com.ecaservice.core.redelivery.AbstractJpaTest;
import com.ecaservice.core.redelivery.aspect.RetryAspect;
import com.ecaservice.core.redelivery.config.RedeliveryProperties;
import com.ecaservice.core.redelivery.metrics.RetryMeterService;
import com.ecaservice.core.redelivery.repository.RetryRequestRepository;
import com.ecaservice.core.redelivery.test.RedeliverTestConfiguration;
import com.ecaservice.core.redelivery.test.model.TestRequest;
import com.ecaservice.core.redelivery.test.service.TestServiceA;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;

import javax.inject.Inject;

import static com.ecaservice.core.redelivery.TestHelperUtils.createRetryRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;

/**
 * Unit tests for {@link RetryService} class.
 *
 * @author Roman Batygin
 */
@EnableAspectJAutoProxy
@Import({RetryService.class, RetryRequestCacheService.class, RedeliveryProperties.class, RetryAspect.class,
        RedeliverTestConfiguration.class})
class RetryServiceTest extends AbstractJpaTest {

    private static final String REQUEST_1 = "REQUEST_1";
    private static final int MAX_RETRIES = 2;

    @SpyBean
    private TestServiceA testServiceA;

    @MockBean
    private RetryMeterService retryMeterService;

    @Inject
    private RetryRequestRepository retryRequestRepository;

    @Inject
    private RetryService retryService;

    @Override
    public void deleteAll() {
        retryRequestRepository.deleteAll();
    }

    @Test
    void testSuccessRetry() {
        var retryRequest = createRetryRequest(REQUEST_1, new TestRequest(), 0, MAX_RETRIES);
        retryRequestRepository.save(retryRequest);
        retryService.retry(retryRequest);
        assertThat(retryRequestRepository.findById(retryRequest.getId())).isNotPresent();
    }

    @Test
    void testRetryWithError() {
        var testRequest = new TestRequest();
        var retryRequest = createRetryRequest(REQUEST_1, testRequest, 0, MAX_RETRIES);
        retryRequestRepository.save(retryRequest);
        doThrow(RuntimeException.class).when(testServiceA).method(testRequest);
        retryService.retry(retryRequest);
        var actual = retryRequestRepository.findById(retryRequest.getId()).orElse(null);
        assertThat(actual).isNotNull();
        assertThat(actual.getRetries()).isOne();
    }

    @Test
    void testRetryExceeded() {
        var testRequest = new TestRequest();
        var retryRequest = createRetryRequest(REQUEST_1, testRequest, MAX_RETRIES - 1, MAX_RETRIES);
        retryRequestRepository.save(retryRequest);
        doThrow(RuntimeException.class).when(testServiceA).method(testRequest);
        retryService.retry(retryRequest);
        assertThat(retryRequestRepository.findById(retryRequest.getId())).isNotPresent();
    }
}
