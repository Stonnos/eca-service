package com.ecaservice.server.service.evaluation;

import com.ecaservice.server.AssertionUtils;
import com.ecaservice.server.TestHelperUtils;
import com.ecaservice.base.model.EvaluationRequest;
import com.ecaservice.base.model.EvaluationResponse;
import com.ecaservice.base.model.TechnicalStatus;
import com.ecaservice.server.config.CrossValidationConfig;
import com.ecaservice.server.configuation.ExecutorConfiguration;
import com.ecaservice.server.mapping.ClassifierInfoMapperImpl;
import com.ecaservice.server.mapping.DateTimeConverter;
import com.ecaservice.server.mapping.EvaluationLogMapper;
import com.ecaservice.server.mapping.EvaluationLogMapperImpl;
import com.ecaservice.server.mapping.InstancesInfoMapperImpl;
import com.ecaservice.server.model.entity.EvaluationLog;
import com.ecaservice.server.model.entity.RequestStatus;
import com.ecaservice.server.repository.EvaluationLogRepository;
import com.ecaservice.server.service.AbstractJpaTest;
import com.ecaservice.server.service.evaluation.initializers.ClassifierInitializerService;
import eca.core.evaluation.EvaluationMethod;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.context.annotation.Import;

import javax.inject.Inject;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

/**
 * Unit tests that checks EvaluationRequestService functionality {@see EvaluationRequestService}.
 *
 * @author Roman Batygin
 */
@Import({ExecutorConfiguration.class, CrossValidationConfig.class,
        EvaluationLogMapperImpl.class, EvaluationService.class, DateTimeConverter.class,
        InstancesInfoMapperImpl.class, ClassifierInfoMapperImpl.class})
class EvaluationRequestServiceTest extends AbstractJpaTest {

    @Inject
    private CrossValidationConfig crossValidationConfig;
    @Inject
    private EvaluationLogRepository evaluationLogRepository;
    @Inject
    private EvaluationLogMapper evaluationLogMapper;
    @Inject
    private EvaluationService evaluationService;
    @Inject
    private CalculationExecutorService calculationExecutorService;

    @Mock
    private ClassifierInitializerService classifierInitializerService;

    private EvaluationRequestService evaluationRequestService;

    @Override
    public void init() {
        evaluationRequestService =
                new EvaluationRequestService(crossValidationConfig, calculationExecutorService, evaluationService,
                        evaluationLogRepository, evaluationLogMapper, classifierInitializerService);
    }

    @Override
    public void deleteAll() {
        evaluationLogRepository.deleteAll();
    }

    @Test
    void testSuccessClassification() {
        EvaluationRequest request = TestHelperUtils.createEvaluationRequest();
        EvaluationResponse evaluationResponse = evaluationRequestService.processRequest(request);
        assertThat(evaluationResponse.getStatus()).isEqualTo(TechnicalStatus.SUCCESS);
        List<EvaluationLog> evaluationLogList = evaluationLogRepository.findAll();
        AssertionUtils.hasOneElement(evaluationLogList);
        assertThat(evaluationLogList.get(0).getRequestStatus()).isEqualTo(RequestStatus.FINISHED);
        assertThat(evaluationResponse.getStatus()).isEqualTo(TechnicalStatus.SUCCESS);
        assertThat(evaluationResponse.getEvaluationResults()).isNotNull();
        assertThat(evaluationResponse.getEvaluationResults().getClassifier()).isNotNull();
        assertThat(evaluationResponse.getEvaluationResults().getEvaluation()).isNotNull();
    }

    @Test
    void testClassificationWithException() throws Exception {
        EvaluationRequest request = TestHelperUtils.createEvaluationRequest();
        CalculationExecutorServiceImpl executorService = mock(CalculationExecutorServiceImpl.class);
        EvaluationRequestService service =
                new EvaluationRequestService(crossValidationConfig, executorService, evaluationService,
                        evaluationLogRepository, evaluationLogMapper, classifierInitializerService);
        doThrow(new RuntimeException("Error")).when(executorService)
                .execute(any(), anyLong(), any(TimeUnit.class));
        EvaluationResponse evaluationResponse = service.processRequest(request);
        assertThat(evaluationResponse.getStatus()).isEqualTo(TechnicalStatus.ERROR);
        List<EvaluationLog> evaluationLogList = evaluationLogRepository.findAll();
        AssertionUtils.hasOneElement(evaluationLogList);
        assertThat(evaluationLogList.get(0).getRequestStatus()).isEqualTo(RequestStatus.ERROR);
        assertThat(evaluationResponse.getStatus()).isEqualTo(TechnicalStatus.ERROR);
        assertThat(evaluationResponse.getEvaluationResults()).isNull();
    }

    @Test
    void testClassificationWithError() {
        EvaluationRequest request = TestHelperUtils.createEvaluationRequest();
        request.setEvaluationMethod(EvaluationMethod.CROSS_VALIDATION);
        request.setNumFolds(1);
        EvaluationResponse evaluationResponse = evaluationRequestService.processRequest(request);
        assertThat(evaluationResponse.getStatus()).isEqualTo(TechnicalStatus.ERROR);
        List<EvaluationLog> evaluationLogList = evaluationLogRepository.findAll();
        AssertionUtils.hasOneElement(evaluationLogList);
        assertThat(evaluationLogList.get(0).getRequestStatus()).isEqualTo(RequestStatus.ERROR);
        assertThat(evaluationResponse.getStatus()).isEqualTo(TechnicalStatus.ERROR);
        assertThat(evaluationResponse.getEvaluationResults()).isNull();
    }

    @Test
    void testTimeoutInClassification() throws Exception {
        EvaluationRequest request = TestHelperUtils.createEvaluationRequest();
        CalculationExecutorServiceImpl executorService = mock(CalculationExecutorServiceImpl.class);
        EvaluationRequestService service =
                new EvaluationRequestService(crossValidationConfig, executorService, evaluationService,
                        evaluationLogRepository, evaluationLogMapper, classifierInitializerService);
        doThrow(TimeoutException.class).when(executorService).execute(any(), anyLong(), any(TimeUnit.class));
        EvaluationResponse evaluationResponse = service.processRequest(request);
        assertThat(evaluationResponse.getStatus()).isEqualTo(TechnicalStatus.TIMEOUT);
        List<EvaluationLog> evaluationLogList = evaluationLogRepository.findAll();
        AssertionUtils.hasOneElement(evaluationLogList);
        assertThat(evaluationLogList.get(0).getRequestStatus()).isEqualTo(RequestStatus.TIMEOUT);
        assertThat(evaluationResponse.getStatus()).isEqualTo(TechnicalStatus.TIMEOUT);
        assertThat(evaluationResponse.getEvaluationResults()).isNull();
    }
}
