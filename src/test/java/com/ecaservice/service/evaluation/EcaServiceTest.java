package com.ecaservice.service.evaluation;

import com.ecaservice.AssertionUtils;
import com.ecaservice.TestHelperUtils;
import com.ecaservice.config.CrossValidationConfig;
import com.ecaservice.dto.EvaluationResponse;
import com.ecaservice.exception.EcaServiceException;
import com.ecaservice.mapping.EvaluationLogMapper;
import com.ecaservice.model.TechnicalStatus;
import com.ecaservice.model.entity.EvaluationLog;
import com.ecaservice.model.evaluation.EvaluationMethod;
import com.ecaservice.model.evaluation.EvaluationOption;
import com.ecaservice.model.evaluation.EvaluationRequest;
import com.ecaservice.model.evaluation.EvaluationStatus;
import com.ecaservice.repository.EvaluationLogRepository;
import eca.core.evaluation.Evaluation;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

import javax.inject.Inject;
import java.util.EnumMap;
import java.util.List;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests that checks EcaService functionality {@see EcaService}.
 *
 * @author Roman Batygin
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class EcaServiceTest {

    @Inject
    private CrossValidationConfig crossValidationConfig;
    @Inject
    private EvaluationLogRepository evaluationLogRepository;
    @Inject
    private EvaluationLogMapper evaluationLogMapper;
    @Inject
    private EvaluationService evaluationService;

    private EcaService ecaService;

    @Before
    public void setUp() {
        evaluationLogRepository.deleteAll();
        CalculationExecutorService calculationExecutorService =
                new CalculationExecutorServiceImpl(Executors.newCachedThreadPool());
        ecaService = new EcaService(crossValidationConfig, calculationExecutorService, evaluationService,
                evaluationLogRepository, evaluationLogMapper);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullEvaluationRequest() {
        ecaService.processRequest(null);
    }

    @Test
    public void testSuccessClassification() throws Exception {
        EvaluationRequest request = TestHelperUtils.createEvaluationRequest(TestHelperUtils.IP_ADDRESS);
        EvaluationResponse evaluationResponse = ecaService.processRequest(request);
        assertThat(evaluationResponse.getStatus()).isEqualTo(TechnicalStatus.SUCCESS);
        List<EvaluationLog> evaluationLogList = evaluationLogRepository.findAll();
        AssertionUtils.assertSingletonList(evaluationLogList);
        assertThat(evaluationLogList.get(0).getEvaluationStatus()).isEqualTo(EvaluationStatus.FINISHED);
        assertThat(evaluationResponse.getStatus()).isEqualTo(TechnicalStatus.SUCCESS);
        assertThat(evaluationResponse.getEvaluationResults()).isNotNull();
        assertThat(evaluationResponse.getEvaluationResults().getClassifier()).isNotNull();
        assertThat(evaluationResponse.getEvaluationResults().getEvaluation()).isNotNull();
    }

    @Test
    public void testClassificationWithException() throws Exception {
        EvaluationRequest request = TestHelperUtils.createEvaluationRequest(TestHelperUtils.IP_ADDRESS);
        CalculationExecutorServiceImpl executorService = mock(CalculationExecutorServiceImpl.class);
        EcaService service = new EcaService(crossValidationConfig, executorService, evaluationService,
                evaluationLogRepository, evaluationLogMapper);
        doThrow(new EcaServiceException("Error")).when(executorService)
                .execute(anyObject(), anyLong(), any(TimeUnit.class));
        EvaluationResponse evaluationResponse = service.processRequest(request);
        assertThat(evaluationResponse.getStatus()).isEqualTo(TechnicalStatus.ERROR);
        List<EvaluationLog> evaluationLogList = evaluationLogRepository.findAll();
        AssertionUtils.assertSingletonList(evaluationLogList);
        assertThat(evaluationLogList.get(0).getEvaluationStatus()).isEqualTo(EvaluationStatus.ERROR);
        assertThat(evaluationResponse.getStatus()).isEqualTo(TechnicalStatus.ERROR);
        assertThat(evaluationResponse.getEvaluationResults()).isNull();
    }

    @Test
    public void testClassificationWithError() throws Exception {
        EvaluationRequest request = TestHelperUtils.createEvaluationRequest(TestHelperUtils.IP_ADDRESS);
        request.setEvaluationMethod(EvaluationMethod.CROSS_VALIDATION);
        request.setEvaluationOptionsMap(new EnumMap<>(EvaluationOption.class));
        request.getEvaluationOptionsMap().put(EvaluationOption.NUM_FOLDS, String.valueOf(1));
        EvaluationResponse evaluationResponse = ecaService.processRequest(request);
        assertThat(evaluationResponse.getStatus()).isEqualTo(TechnicalStatus.ERROR);
        List<EvaluationLog> evaluationLogList = evaluationLogRepository.findAll();
        AssertionUtils.assertSingletonList(evaluationLogList);
        assertThat(evaluationLogList.get(0).getEvaluationStatus()).isEqualTo(EvaluationStatus.ERROR);
        assertThat(evaluationResponse.getStatus()).isEqualTo(TechnicalStatus.ERROR);
        assertThat(evaluationResponse.getEvaluationResults()).isNull();
    }

    @Test
    public void testTimeoutInClassification() throws Exception {
        EvaluationRequest request = TestHelperUtils.createEvaluationRequest(TestHelperUtils.IP_ADDRESS);
        CalculationExecutorServiceImpl executorService = mock(CalculationExecutorServiceImpl.class);
        EcaService service = new EcaService(crossValidationConfig, executorService, evaluationService,
                evaluationLogRepository, evaluationLogMapper);
        doThrow(TimeoutException.class).when(executorService)
                .execute(anyObject(), anyLong(), any(TimeUnit.class));
        EvaluationResponse evaluationResponse = service.processRequest(request);
        assertThat(evaluationResponse.getStatus()).isEqualTo(TechnicalStatus.TIMEOUT);
        List<EvaluationLog> evaluationLogList = evaluationLogRepository.findAll();
        AssertionUtils.assertSingletonList(evaluationLogList);
        assertThat(evaluationLogList.get(0).getEvaluationStatus()).isEqualTo(EvaluationStatus.TIMEOUT);
        assertThat(evaluationResponse.getStatus()).isEqualTo(TechnicalStatus.TIMEOUT);
        assertThat(evaluationResponse.getEvaluationResults()).isNull();
    }
}
