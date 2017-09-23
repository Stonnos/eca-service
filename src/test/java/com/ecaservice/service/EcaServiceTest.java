package com.ecaservice.service;

import com.ecaservice.TestDataBuilder;
import com.ecaservice.config.CrossValidationConfig;
import com.ecaservice.dto.EvaluationResponse;
import com.ecaservice.dto.TechnicalStatus;
import com.ecaservice.mapping.ClassifierToInputOptionsListConverter;
import com.ecaservice.mapping.EvaluationRequestToEvaluationLogConverter;
import com.ecaservice.mapping.InstancesToInstancesInfoConverter;
import com.ecaservice.mapping.OrikaBeanMapper;
import com.ecaservice.model.EvaluationRequest;
import com.ecaservice.model.entity.EvaluationLog;
import com.ecaservice.model.entity.EvaluationStatus;
import com.ecaservice.repository.EvaluationLogRepository;
import com.ecaservice.service.impl.CalculationExecutorServiceImpl;
import com.ecaservice.service.impl.EcaServiceImpl;
import com.ecaservice.service.impl.EvaluationServiceImpl;
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
@ContextConfiguration(classes = {OrikaBeanMapper.class,
        InstancesToInstancesInfoConverter.class,
        ClassifierToInputOptionsListConverter.class,
        EvaluationRequestToEvaluationLogConverter.class})
@AutoConfigureDataJpa
@EnableJpaRepositories(basePackageClasses = EvaluationLogRepository.class)
@EntityScan(basePackageClasses = EvaluationLog.class)
@EnableConfigurationProperties
@TestPropertySource("classpath:application-test.properties")
public class EcaServiceTest {

    private static final int NUM_INSTANCES = 25;
    private static final int NUM_ATTRIBUTES = 6;
    private static final int SEED = 3;
    private static final int NUM_FOLDS = 10;
    private static final int NUM_TEST = 10;
    private static final String IP = "127.0.0.1";

    @Mock
    private CrossValidationConfig crossValidationConfig;
    @Autowired
    private EvaluationLogRepository evaluationLogRepository;

    @Autowired
    private OrikaBeanMapper mapper;

    private EvaluationService evaluationService;

    private ExecutorService executorService;

    private CalculationExecutorService calculationExecutorService;

    private EcaService ecaService;

    @Before
    public void setUp() {
        when(crossValidationConfig.getSeed()).thenReturn(SEED);
        when(crossValidationConfig.getNumFolds()).thenReturn(NUM_FOLDS);
        when(crossValidationConfig.getNumTests()).thenReturn(NUM_TEST);

        executorService = Executors.newCachedThreadPool();
        calculationExecutorService = new CalculationExecutorServiceImpl(executorService);

        evaluationService = new EvaluationServiceImpl(crossValidationConfig);

        ecaService = new EcaServiceImpl(crossValidationConfig, calculationExecutorService, evaluationService,
                evaluationLogRepository, mapper);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullEvaluationRequest() {
        ecaService.processRequest(null);
    }

    @Test
    public void testSuccessClassification() {

        EvaluationRequest request = TestDataBuilder.createEvaluationRequest(IP, NUM_INSTANCES, NUM_ATTRIBUTES);

        EvaluationResponse evaluationResponse = ecaService.processRequest(request);

        assertEquals(evaluationResponse.getStatus(), TechnicalStatus.SUCCESS);

        List<EvaluationLog> evaluationLogList = evaluationLogRepository.findAll();

        assertEquals(evaluationLogList.size(), 1);
        assertEquals(evaluationLogList.get(0).getEvaluationStatus(), EvaluationStatus.FINISHED);

        evaluationLogRepository.deleteAll();

    }

    @Test
    public void testErrorClassification() {

        EvaluationRequest request = TestDataBuilder.createEvaluationRequest(IP, NUM_INSTANCES, NUM_ATTRIBUTES);

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

        EvaluationRequest request = TestDataBuilder.createEvaluationRequest(IP, NUM_INSTANCES, NUM_ATTRIBUTES);

        when(crossValidationConfig.getTimeout()).thenThrow(TimeoutException.class);

        EvaluationResponse evaluationResponse = ecaService.processRequest(request);

        assertEquals(evaluationResponse.getStatus(), TechnicalStatus.TIMEOUT);

        List<EvaluationLog> evaluationLogList = evaluationLogRepository.findAll();

        assertEquals(evaluationLogList.size(), 1);
        assertEquals(evaluationLogList.get(0).getEvaluationStatus(), EvaluationStatus.TIMEOUT);

        evaluationLogRepository.deleteAll();

    }

}
