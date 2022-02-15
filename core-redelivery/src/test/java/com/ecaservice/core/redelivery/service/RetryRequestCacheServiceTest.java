package com.ecaservice.core.redelivery.service;

import com.ecaservice.core.redelivery.AbstractJpaTest;
import com.ecaservice.core.redelivery.repository.RetryRequestRepository;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;

import javax.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link RetryRequestCacheService} class.
 *
 * @author Roman Batygin
 */
@Import(RetryRequestCacheService.class)
class RetryRequestCacheServiceTest extends AbstractJpaTest {

    private static final String REQUEST_TYPE = "REQUEST_TYPE";
    private static final String REQUEST = "request";

    @Inject
    private RetryRequestRepository retryRequestRepository;

    @Inject
    private RetryRequestCacheService retryRequestCacheService;

    @Override
    public void deleteAll() {
        retryRequestRepository.deleteAll();
    }

    @Test
    void testSaveRetryRequest() {
        retryRequestCacheService.save(REQUEST_TYPE, REQUEST, 0);
        var requests = retryRequestRepository.findAll();
        assertThat(requests).hasSize(1);
        var actual = requests.iterator().next();
        assertThat(actual).isNotNull();
        assertThat(actual.getRequestType()).isEqualTo(REQUEST_TYPE);
        assertThat(actual.getRequest()).isEqualTo(REQUEST);
        assertThat(actual.getMaxRetries()).isZero();
        assertThat(actual.getCreatedAt()).isNotNull();
    }

    @Test
    void testDeleteRequest() {
        retryRequestCacheService.save(REQUEST_TYPE, REQUEST, 0);
        var requests = retryRequestRepository.findAll();
        assertThat(requests).hasSize(1);
        var retryRequest = requests.iterator().next();
        retryRequestCacheService.delete(retryRequest);
        assertThat(retryRequestRepository.findById(retryRequest.getId())).isNotPresent();
    }
}
