package com.ecaservice.server.service.ers;

import com.ecaservice.classifier.options.config.ClassifiersOptionsAutoConfiguration;
import com.ecaservice.ers.dto.GetEvaluationResultsRequest;
import com.ecaservice.ers.dto.GetEvaluationResultsResponse;
import com.ecaservice.server.TestHelperUtils;
import com.ecaservice.server.config.CrossValidationConfig;
import com.ecaservice.server.config.cache.CacheNames;
import com.ecaservice.server.config.ers.ErsConfig;
import com.ecaservice.server.configuation.CacheConfiguration;
import com.ecaservice.server.mapping.ClassifierReportMapperImpl;
import com.ecaservice.server.mapping.ErsResponseStatusMapperImpl;
import com.ecaservice.server.mapping.InstancesInfoMapperImpl;
import com.ecaservice.server.service.AbstractJpaTest;
import com.ecaservice.server.service.evaluation.EvaluationResultsService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Import;

import javax.inject.Inject;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for checking evaluation results caching functionality.
 *
 * @author Roman Batygin
 */
@Import({ErsConfig.class, ClassifierReportMapperImpl.class, ErsErrorHandler.class,
        EvaluationResultsService.class, ErsResponseStatusMapperImpl.class,
        CacheConfiguration.class, CrossValidationConfig.class, ErsRequestService.class,
        ClassifiersOptionsAutoConfiguration.class, InstancesInfoMapperImpl.class})
class GetEvaluationResultsCacheTest extends AbstractJpaTest {

    @MockBean
    private ErsClient ersClient;
    @MockBean
    private ErsRequestSender ersRequestSender;

    @Inject
    private ErsRequestService ersRequestService;
    @Inject
    private CacheManager cacheManager;

    @Test
    void testGetEvaluationResultsCache() {
        String requestId = UUID.randomUUID().toString();
        GetEvaluationResultsResponse first =
                TestHelperUtils.createGetEvaluationResultsResponse(requestId);
        GetEvaluationResultsResponse second =
                TestHelperUtils.createGetEvaluationResultsResponse(requestId);
        when(ersClient.getEvaluationResults(any(GetEvaluationResultsRequest.class)))
                .thenReturn(first)
                .thenReturn(second);
        //Checks first call with ERROR status (Results shouldn't save in cache)
        GetEvaluationResultsResponse actual =
                ersRequestService.getEvaluationResults(requestId);
        Assertions.assertThat(actual).isNotNull();
        Assertions.assertThat(actual.getRequestId()).isEqualTo(first.getRequestId());
        //Checks second call
        actual = ersRequestService.getEvaluationResults(requestId);
        Assertions.assertThat(actual).isNotNull();
        Assertions.assertThat(actual.getRequestId()).isEqualTo(second.getRequestId());
        //Method must calls once because of cache
        verify(ersClient, atLeastOnce()).getEvaluationResults(any(GetEvaluationResultsRequest.class));
        Assertions.assertThat(
                cacheManager.getCache(CacheNames.EVALUATION_RESULTS_CACHE_NAME).get(requestId)).isNotNull();
    }
}
