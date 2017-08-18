package com.ecaservice.service;

import com.ecaservice.TestDataBuilder;
import com.ecaservice.config.DataBaseConfig;
import com.ecaservice.mapping.ClassificationResultToEvaluationLogConverter;
import com.ecaservice.mapping.ClassifierToInputOptionsListConverter;
import com.ecaservice.mapping.InstancesToInstancesInfoConverter;
import com.ecaservice.mapping.OrikaBeanMapper;
import com.ecaservice.model.ClassificationResult;
import com.ecaservice.model.EvaluationMethod;
import com.ecaservice.model.entity.EvaluationLog;
import com.ecaservice.repository.EvaluationLogRepository;
import com.ecaservice.service.impl.EvaluationLogServiceImpl;
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

import java.time.LocalDateTime;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

/**
 * @author Roman Batygin
 */
@RunWith(SpringRunner.class)
@AutoConfigureDataJpa
@ContextConfiguration(classes = {EvaluationLogRepository.class,
        OrikaBeanMapper.class,
        InstancesToInstancesInfoConverter.class,
        ClassificationResultToEvaluationLogConverter.class,
        ClassifierToInputOptionsListConverter.class
})
@EnableJpaRepositories(basePackageClasses = EvaluationLogRepository.class)
@EntityScan(basePackageClasses = EvaluationLog.class)
@EnableConfigurationProperties
@TestPropertySource("classpath:application-test.properties")
public class EvaluationLogServiceTest {

    private static final int NUM_INSTANCES = 25;
    private static final int NUM_ATTRIBUTES = 6;
    private static final int NUM_FOLDS = 10;
    private static final int NUM_TESTS = 10;
    private static final String IP = "127.0.0.1";

    @Autowired
    private EvaluationLogRepository logRepository;

    @Mock
    private DataBaseConfig config;

    @Autowired
    private OrikaBeanMapper mapper;

    private EvaluationLogService logService;

    @Before
    public void setUp() {
        logService = new EvaluationLogServiceImpl(logRepository, config, mapper);
    }

    @Test
    public void testDatabaseDisabled() throws Exception {
        when(config.getEnabled()).thenReturn(false);

        ClassificationResult result =
                TestDataBuilder.createClassificationResult(NUM_INSTANCES, NUM_ATTRIBUTES);

        logService.save(result, EvaluationMethod.TRAINING_DATA,
                NUM_FOLDS, NUM_TESTS, LocalDateTime.now(), IP);

        assertTrue(logRepository.findAll().isEmpty());
    }
}
