package com.ecaservice.service.ers;

import com.ecaservice.TestHelperUtils;
import com.ecaservice.config.CrossValidationConfig;
import com.ecaservice.config.CacheNames;
import com.ecaservice.config.ers.ErsConfig;
import com.ecaservice.configuation.CacheConfiguration;
import com.ecaservice.configuation.ClassifierOptionsMapperConfiguration;
import com.ecaservice.configuation.ErsWebServiceConfiguration;
import com.ecaservice.dto.evaluation.GetEvaluationResultsRequest;
import com.ecaservice.dto.evaluation.GetEvaluationResultsResponse;
import com.ecaservice.dto.evaluation.ResponseStatus;
import com.ecaservice.mapping.ClassifierReportMapperImpl;
import com.ecaservice.mapping.EvaluationResultsMapperImpl;
import com.ecaservice.mapping.InstancesConverter;
import com.ecaservice.service.AbstractJpaTest;
import com.ecaservice.service.ClassifierOptionsService;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.util.ReflectionTestUtils;

import javax.inject.Inject;
import java.util.UUID;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for checking evaluation results caching functionality.
 *
 * @author Roman Batygin
 */
@Import({ErsConfig.class, ClassifierReportMapperImpl.class, ErsWebServiceClient.class,
        EvaluationResultsMapperImpl.class, ErsWebServiceConfiguration.class,
        CacheConfiguration.class, CrossValidationConfig.class, ErsRequestService.class,
        ClassifierOptionsService.class, ClassifierOptionsMapperConfiguration.class, InstancesConverter.class})
public class GetEvaluationResultsCacheTest extends AbstractJpaTest {

    @Inject
    private ErsRequestService ersRequestService;
    @Inject
    private CacheManager cacheManager;
    @Mock
    private ErsWebServiceClient ersWebServiceClient;

    @Test
    public void testGetEvaluationResultsCache() {
        ReflectionTestUtils.setField(ersRequestService, "ersWebServiceClient", ersWebServiceClient);
        String requestId = UUID.randomUUID().toString();
        GetEvaluationResultsResponse first =
                TestHelperUtils.createGetEvaluationResultsSimpleResponse(requestId, ResponseStatus.ERROR);
        GetEvaluationResultsResponse second =
                TestHelperUtils.createGetEvaluationResultsSimpleResponse(requestId, ResponseStatus.SUCCESS);
        GetEvaluationResultsResponse third =
                TestHelperUtils.createGetEvaluationResultsSimpleResponse(requestId, ResponseStatus.SUCCESS);
        when(ersWebServiceClient.getEvaluationResultsSimpleResponse(
                any(GetEvaluationResultsRequest.class))).thenReturn(first).thenReturn(second).thenReturn(third);
        //Checks first call with ERROR status (Results shouldn't save in cache)
        GetEvaluationResultsResponse actual =
                ersRequestService.getEvaluationResults(requestId);
        Assertions.assertThat(actual).isNotNull();
        Assertions.assertThat(actual.getRequestId()).isEqualTo(first.getRequestId());
        Assertions.assertThat(actual.getStatus()).isEqualTo(first.getStatus());
        //Checks second call
        actual = ersRequestService.getEvaluationResults(requestId);
        Assertions.assertThat(actual).isNotNull();
        Assertions.assertThat(actual.getRequestId()).isEqualTo(second.getRequestId());
        Assertions.assertThat(actual.getStatus()).isEqualTo(second.getStatus());
        //Checks third call
        actual = ersRequestService.getEvaluationResults(requestId);
        Assertions.assertThat(actual).isNotNull();
        Assertions.assertThat(actual.getRequestId()).isEqualTo(second.getRequestId());
        Assertions.assertThat(actual.getStatus()).isEqualTo(second.getStatus());
        //Method must calls 2 times because of cache
        verify(ersWebServiceClient, times(2)).getEvaluationResultsSimpleResponse(any
                (GetEvaluationResultsRequest.class));
        Assertions.assertThat(
                cacheManager.getCache(CacheNames.EVALUATION_RESULTS_CACHE_NAME).get(requestId)).isNotNull();
    }
}
