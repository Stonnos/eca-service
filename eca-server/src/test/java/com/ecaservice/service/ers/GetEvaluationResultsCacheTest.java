package com.ecaservice.service.ers;

import com.ecaservice.TestHelperUtils;
import com.ecaservice.classifier.options.config.ClassifiersOptionsAutoConfiguration;
import com.ecaservice.config.CrossValidationConfig;
import com.ecaservice.config.cache.CacheNames;
import com.ecaservice.config.ers.ErsConfig;
import com.ecaservice.configuation.CacheConfiguration;
import com.ecaservice.ers.dto.GetEvaluationResultsRequest;
import com.ecaservice.ers.dto.GetEvaluationResultsResponse;
import com.ecaservice.mapping.ClassifierReportMapperImpl;
import com.ecaservice.mapping.ErsResponseStatusMapperImpl;
import com.ecaservice.mapping.InstancesConverter;
import com.ecaservice.service.AbstractJpaTest;
import com.ecaservice.service.evaluation.EvaluationResultsService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.util.ReflectionTestUtils;

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
@Import({ErsConfig.class, ClassifierReportMapperImpl.class, ErsRequestSender.class,
        EvaluationResultsService.class, ErsResponseStatusMapperImpl.class,
        CacheConfiguration.class, CrossValidationConfig.class, ErsRequestService.class,
        ClassifiersOptionsAutoConfiguration.class, InstancesConverter.class})
class GetEvaluationResultsCacheTest extends AbstractJpaTest {

    @MockBean
    private ErsClient ersClient;

    @Inject
    private ErsRequestService ersRequestService;
    @Inject
    private CacheManager cacheManager;
    @Mock
    private ErsRequestSender ersRequestSender;

    @Test
    void testGetEvaluationResultsCache() {
        ReflectionTestUtils.setField(ersRequestService, "ersRequestSender", ersRequestSender);
        String requestId = UUID.randomUUID().toString();
        GetEvaluationResultsResponse first =
                TestHelperUtils.createGetEvaluationResultsResponse(requestId);
        GetEvaluationResultsResponse second =
                TestHelperUtils.createGetEvaluationResultsResponse(requestId);
        when(ersRequestSender.getEvaluationResultsSimpleResponse(any(GetEvaluationResultsRequest.class)))
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
        verify(ersRequestSender, atLeastOnce()).getEvaluationResultsSimpleResponse(
                any(GetEvaluationResultsRequest.class));
        Assertions.assertThat(
                cacheManager.getCache(CacheNames.EVALUATION_RESULTS_CACHE_NAME).get(requestId)).isNotNull();
    }
}
