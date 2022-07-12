package com.ecaservice.core.redelivery.aspect;

import com.ecaservice.core.redelivery.AbstractJpaTest;
import com.ecaservice.core.redelivery.config.RedeliveryProperties;
import com.ecaservice.core.redelivery.metrics.RetryMeterService;
import com.ecaservice.core.redelivery.repository.RetryRequestRepository;
import com.ecaservice.core.redelivery.service.RetryRequestCacheService;
import com.ecaservice.core.redelivery.test.RedeliverTestConfiguration;
import com.ecaservice.core.redelivery.test.model.TestRequest;
import com.ecaservice.core.redelivery.test.service.TestServiceA;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;

import javax.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;

/**
 * Unit tests for {@link RetryAspect} class.
 *
 * @author Roman Batygin
 */
@EnableAspectJAutoProxy
@Import({RetryRequestCacheService.class, RedeliveryProperties.class, RetryAspect.class,
        RedeliverTestConfiguration.class})
class RetryAspectTest extends AbstractJpaTest {

    private static final String EXPECTED_REQUEST_TYPE = "REQUEST_1";

    @Inject
    private RetryRequestRepository retryRequestRepository;

    @MockBean
    private RetryMeterService retryMeterService;

    @SpyBean
    private TestServiceA testServiceA;

    @Override
    public void deleteAll() {
        retryRequestRepository.deleteAll();
    }

    @Test
    void testSuccessAroundMethod() {
        testServiceA.method(new TestRequest());
        assertThat(retryRequestRepository.count()).isZero();
    }

    @Test
    void testAroundMethodWithError() {
        doThrow(RuntimeException.class).when(testServiceA).method(new TestRequest());
        try {
            testServiceA.method(new TestRequest());
        } catch (Exception ex) {
            //ignored
        }
        var requests = retryRequestRepository.findAll();
        assertThat(requests).hasSize(1);
        var actual = requests.iterator().next();
        assertThat(actual).isNotNull();
        assertThat(actual.getRequestType()).isEqualTo(EXPECTED_REQUEST_TYPE);
    }
}
