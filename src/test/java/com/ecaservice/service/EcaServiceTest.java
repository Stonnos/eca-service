package com.ecaservice.service;

import com.ecaservice.TestDataHelper;
import com.ecaservice.config.CrossValidationConfig;
import com.ecaservice.dto.EvaluationResponse;
import com.ecaservice.mapping.ClassifierToInputOptionsMapConverter;
import com.ecaservice.mapping.EvaluationRequestToEvaluationLogConverter;
import com.ecaservice.mapping.InstancesToInstancesInfoConverter;
import com.ecaservice.mapping.mapstruct.EvaluationLogMapper;
import com.ecaservice.mapping.mapstruct.EvaluationLogMapperImpl;
import com.ecaservice.model.TechnicalStatus;
import com.ecaservice.model.entity.EvaluationLog;
import com.ecaservice.model.evaluation.EvaluationRequest;
import com.ecaservice.model.evaluation.EvaluationStatus;
import com.ecaservice.repository.EvaluationLogRepository;
import com.ecaservice.service.evaluation.CalculationExecutorService;
import com.ecaservice.service.evaluation.EcaService;
import com.ecaservice.service.evaluation.EvaluationService;
import com.ecaservice.service.evaluation.impl.CalculationExecutorServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

/**
 * Unit tests that checks EvaluationService functionality (see {@link EcaService}).
 *
 * @author Roman Batygin
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {EvaluationLogMapperImpl.class,
        InstancesToInstancesInfoConverter.class,
        ClassifierToInputOptionsMapConverter.class,
        EvaluationRequestToEvaluationLogConverter.class})
@AutoConfigureDataJpa
@EnableJpaRepositories(basePackageClasses = EvaluationLogRepository.class)
@EntityScan(basePackageClasses = EvaluationLog.class)
@EnableConfigurationProperties
@TestPropertySource("classpath:application-test.properties")
public class EcaServiceTest {

    @Mock
    private CrossValidationConfig crossValidationConfig;
    @Autowired
    private EvaluationLogRepository evaluationLogRepository;

    @Autowired
    private EvaluationLogMapper evaluationLogMapper;

    private EvaluationService evaluationService;

    private ExecutorService executorService;

    private CalculationExecutorService calculationExecutorService;

    private EcaService ecaService;

    @Before
    public void setUp() {
        when(crossValidationConfig.getSeed()).thenReturn(TestDataHelper.SEED);
        when(crossValidationConfig.getNumFolds()).thenReturn(TestDataHelper.NUM_FOLDS);
        when(crossValidationConfig.getNumTests()).thenReturn(TestDataHelper.NUM_TESTS);
        executorService = Executors.newCachedThreadPool();
        calculationExecutorService = new CalculationExecutorServiceImpl(executorService);
        evaluationService = new EvaluationService(crossValidationConfig);
        ecaService = new EcaService(crossValidationConfig, calculationExecutorService, evaluationService,
                evaluationLogRepository, evaluationLogMapper);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullEvaluationRequest() {
        ecaService.processRequest(null);
    }

    @Test
    public void testSuccessClassification() {
        EvaluationRequest request = TestDataHelper.createEvaluationRequest(TestDataHelper.IP_ADDRESS,
                TestDataHelper.NUM_INSTANCES, TestDataHelper.NUM_ATTRIBUTES);
        EvaluationResponse evaluationResponse = ecaService.processRequest(request);
        assertEquals(evaluationResponse.getStatus(), TechnicalStatus.SUCCESS);
        List<EvaluationLog> evaluationLogList = evaluationLogRepository.findAll();
        assertEquals(evaluationLogList.size(), 1);
        assertEquals(evaluationLogList.get(0).getEvaluationStatus(), EvaluationStatus.FINISHED);
        evaluationLogRepository.deleteAll();
    }

    @Test
    public void testErrorClassification() {
        EvaluationRequest request = TestDataHelper.createEvaluationRequest(TestDataHelper.IP_ADDRESS,
                TestDataHelper.NUM_INSTANCES, TestDataHelper.NUM_ATTRIBUTES);
        when(crossValidationConfig.getTimeout()).thenThrow(Exception.class);
        EvaluationResponse evaluationResponse = ecaService.processRequest(request);
        assertEquals(evaluationResponse.getStatus(), TechnicalStatus.ERROR);
        List<EvaluationLog> evaluationLogList = evaluationLogRepository.findAll();
        assertEquals(evaluationLogList.size(), 1);
        assertEquals(evaluationLogList.get(0).getEvaluationStatus(), EvaluationStatus.ERROR);
        evaluationLogRepository.deleteAll();

    }

    @Test
    public void testTimeoutInClassification() {
        EvaluationRequest request = TestDataHelper.createEvaluationRequest(TestDataHelper.IP_ADDRESS,
                TestDataHelper.NUM_INSTANCES, TestDataHelper.NUM_ATTRIBUTES);
        when(crossValidationConfig.getTimeout()).thenThrow(TimeoutException.class);
        EvaluationResponse evaluationResponse = ecaService.processRequest(request);
        assertEquals(evaluationResponse.getStatus(), TechnicalStatus.TIMEOUT);
        List<EvaluationLog> evaluationLogList = evaluationLogRepository.findAll();
        assertEquals(evaluationLogList.size(), 1);
        assertEquals(evaluationLogList.get(0).getEvaluationStatus(), EvaluationStatus.TIMEOUT);
        evaluationLogRepository.deleteAll();
    }

}
