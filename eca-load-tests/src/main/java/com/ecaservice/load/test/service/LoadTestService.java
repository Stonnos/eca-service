package com.ecaservice.load.test.service;

import com.ecaservice.common.web.exception.EntityNotFoundException;
import com.ecaservice.load.test.config.EcaLoadTestsConfig;
import com.ecaservice.load.test.dto.LoadTestRequest;
import com.ecaservice.load.test.entity.LoadTestEntity;
import com.ecaservice.load.test.repository.LoadTestRepository;
import com.ecaservice.test.common.model.ExecutionStatus;
import eca.core.evaluation.EvaluationMethod;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

/**
 * Load test service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LoadTestService {

    private final EcaLoadTestsConfig ecaLoadTestsConfig;
    private final LoadTestRepository loadTestRepository;

    /**
     * Creates new load test.
     *
     * @param loadTestRequest - load test request
     * @return load test entity
     */
    public LoadTestEntity createTest(LoadTestRequest loadTestRequest) {
        LoadTestEntity loadTestEntity = createLoadTestEntity(loadTestRequest);
        loadTestEntity.setCreated(LocalDateTime.now());
        return loadTestRepository.save(loadTestEntity);
    }

    /**
     * Gets load test entity by uuid.
     *
     * @param testUuid - test uuid
     * @return load test entity
     */
    public LoadTestEntity getLoadTest(String testUuid) {
        return loadTestRepository.findByTestUuid(testUuid)
                .orElseThrow(() -> new EntityNotFoundException(LoadTestEntity.class, testUuid));
    }

    private LoadTestEntity createLoadTestEntity(LoadTestRequest loadTestRequest) {
        Integer numRequests =
                Optional.ofNullable(loadTestRequest.getNumRequests()).orElse(ecaLoadTestsConfig.getNumRequests());
        Integer numThreads =
                Optional.ofNullable(loadTestRequest.getNumThreads()).orElse(ecaLoadTestsConfig.getNumThreads());
        LoadTestEntity loadTestEntity = new LoadTestEntity();
        loadTestEntity.setTestUuid(UUID.randomUUID().toString());
        loadTestEntity.setNumRequests(numRequests);
        loadTestEntity.setNumThreads(numThreads);
        loadTestEntity.setExecutionStatus(ExecutionStatus.NEW);
        initializeOptions(loadTestEntity, loadTestRequest);
        return loadTestEntity;
    }

    private void initializeOptions(LoadTestEntity loadTestEntity, LoadTestRequest loadTestRequest) {
        EvaluationMethod evaluationMethod = loadTestRequest.getEvaluationMethod();
        if (EvaluationMethod.CROSS_VALIDATION.equals(evaluationMethod)) {
            Integer numFolds =
                    Optional.ofNullable(loadTestRequest.getNumFolds()).orElse(ecaLoadTestsConfig.getNumFolds());
            Integer numTests =
                    Optional.ofNullable(loadTestRequest.getNumTests()).orElse(ecaLoadTestsConfig.getNumTests());
            loadTestEntity.setNumFolds(numFolds);
            loadTestEntity.setNumTests(numTests);
        }
        Integer seed = Optional.ofNullable(loadTestRequest.getSeed()).orElse(ecaLoadTestsConfig.getSeed());
        loadTestEntity.setSeed(seed);
        loadTestEntity.setEvaluationMethod(evaluationMethod);
    }
}
