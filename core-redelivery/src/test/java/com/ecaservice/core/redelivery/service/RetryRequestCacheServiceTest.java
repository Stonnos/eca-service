package com.ecaservice.core.redelivery.service;

import com.ecaservice.core.redelivery.AbstractJpaTest;
import com.ecaservice.core.redelivery.model.RetryRequestModel;
import com.ecaservice.core.redelivery.repository.RetryRequestRepository;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;

import javax.inject.Inject;
import java.util.UUID;

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
    private static final long MIN_RETRY_INTERVAL = 30000L;

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
        String requestId = UUID.randomUUID().toString();
        saveRequest(requestId);
        var requests = retryRequestRepository.findAll();
        assertThat(requests).hasSize(1);
        var actual = requests.iterator().next();
        assertThat(actual).isNotNull();
        assertThat(actual.getRequestType()).isEqualTo(REQUEST_TYPE);
        assertThat(actual.getRequest()).isEqualTo(REQUEST);
        assertThat(actual.getRequestId()).isEqualTo(requestId);
        assertThat(actual.getMaxRetries()).isZero();
        assertThat(actual.getCreatedAt()).isNotNull();
        assertThat(actual.getRetryAt()).isNotNull();
    }

    @Test
    void testDeleteRequest() {
        saveRequest(UUID.randomUUID().toString());
        var requests = retryRequestRepository.findAll();
        assertThat(requests).hasSize(1);
        var retryRequest = requests.iterator().next();
        retryRequestCacheService.delete(retryRequest);
        assertThat(retryRequestRepository.findById(retryRequest.getId())).isNotPresent();
    }

    private void saveRequest(String requestId) {
        retryRequestCacheService.save(RetryRequestModel.builder()
                .requestType(REQUEST_TYPE)
                .requestId(requestId)
                .maxRetries(0)
                .minRetryInterval(MIN_RETRY_INTERVAL)
                .build()
        );
    }
}
