package com.ecaservice.service.evaluation;

import com.ecaservice.AssertionUtils;
import com.ecaservice.TestHelperUtils;
import com.ecaservice.base.model.EvaluationRequest;
import com.ecaservice.base.model.EvaluationResponse;
import com.ecaservice.base.model.TechnicalStatus;
import com.ecaservice.config.CommonConfig;
import com.ecaservice.config.CrossValidationConfig;
import com.ecaservice.configuation.ExecutorConfiguration;
import com.ecaservice.mapping.ClassifierInfoMapperImpl;
import com.ecaservice.mapping.DateTimeConverter;
import com.ecaservice.mapping.EvaluationLogMapper;
import com.ecaservice.mapping.EvaluationLogMapperImpl;
import com.ecaservice.mapping.InstancesInfoMapperImpl;
import com.ecaservice.model.entity.EvaluationLog;
import com.ecaservice.model.entity.RequestStatus;
import com.ecaservice.repository.AppInstanceRepository;
import com.ecaservice.repository.EvaluationLogRepository;
import com.ecaservice.service.AbstractJpaTest;
import com.ecaservice.service.AppInstanceService;
import eca.core.evaluation.EvaluationMethod;
import org.junit.jupiter.api.Test;
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
        EvaluationLogMapperImpl.class, EvaluationService.class, CommonConfig.class, DateTimeConverter.class,
        InstancesInfoMapperImpl.class, ClassifierInfoMapperImpl.class})
class EvaluationRequestServiceTest extends AbstractJpaTest {

    @Inject
    private CommonConfig commonConfig;
    @Inject
    private CrossValidationConfig crossValidationConfig;
    @Inject
    private AppInstanceRepository appInstanceRepository;
    @Inject
    private EvaluationLogRepository evaluationLogRepository;
    @Inject
    private EvaluationLogMapper evaluationLogMapper;
    @Inject
    private EvaluationService evaluationService;
    @Inject
    private CalculationExecutorService calculationExecutorService;

    private AppInstanceService appInstanceService;

    private EvaluationRequestService evaluationRequestService;

    @Override
    public void init() {
        appInstanceService = new AppInstanceService(commonConfig, appInstanceRepository);
        evaluationRequestService =
                new EvaluationRequestService(crossValidationConfig, calculationExecutorService, evaluationService,
                        appInstanceService, evaluationLogRepository, evaluationLogMapper);
    }

    @Override
    public void deleteAll() {
        evaluationLogRepository.deleteAll();
        appInstanceRepository.deleteAll();
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
                        appInstanceService, evaluationLogRepository, evaluationLogMapper);
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
                        appInstanceService, evaluationLogRepository, evaluationLogMapper);
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
