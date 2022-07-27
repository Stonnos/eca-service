package com.ecaservice.external.api.controller;

import com.ecaservice.external.api.AbstractJpaTest;
import com.ecaservice.external.api.config.ExternalApiConfig;
import com.ecaservice.external.api.dto.ResponseCode;
import com.ecaservice.external.api.mapping.EcaRequestMapperImpl;
import com.ecaservice.external.api.metrics.MetricsService;
import com.ecaservice.external.api.repository.EvaluationRequestRepository;
import com.ecaservice.external.api.service.EcaRequestService;
import com.ecaservice.external.api.service.RequestStageHandler;
import com.ecaservice.s3.client.minio.service.ObjectStorageService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

import javax.inject.Inject;
import java.util.UUID;

import static com.ecaservice.external.api.TestHelperUtils.createEvaluationRequestEntity;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link TimeoutFallback} class.
 *
 * @author Roman Batygin
 */
@Import({TimeoutFallback.class, RequestStageHandler.class, ExternalApiConfig.class, EcaRequestService.class,
        EcaRequestMapperImpl.class})
class TimeoutFallbackTest extends AbstractJpaTest {

    @MockBean
    private MetricsService metricsService;
    @MockBean
    private ObjectStorageService objectStorageService;

    @Inject
    private EvaluationRequestRepository evaluationRequestRepository;

    @Inject
    private TimeoutFallback timeoutFallback;

    @Override
    public void deleteAll() {
        evaluationRequestRepository.deleteAll();
    }

    @Test
    void testTimeout() {
        var evaluationRequestEntity = createEvaluationRequestEntity(UUID.randomUUID().toString());
        evaluationRequestRepository.save(evaluationRequestEntity);
        var mono = timeoutFallback.timeout(evaluationRequestEntity.getCorrelationId());
        assertThat(mono).isNotNull();
        var responseDto = mono.block();
        assertThat(responseDto).isNotNull();
        assertThat(responseDto.getResponseCode()).isEqualTo(ResponseCode.TIMEOUT);
    }
}
