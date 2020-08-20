package com.ecaservice.load.test.service;

import com.ecaservice.load.test.config.EcaLoadTestsConfig;
import com.ecaservice.load.test.dto.LoadTestRequest;
import com.ecaservice.load.test.entity.ExecutionStatus;
import com.ecaservice.load.test.entity.LoadTestEntity;
import com.ecaservice.load.test.repository.LoadTestRepository;
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
        Integer numRequests =
                Optional.ofNullable(loadTestRequest.getNumRequests()).orElse(ecaLoadTestsConfig.getNumRequests());
        Integer numThreads =
                Optional.ofNullable(loadTestRequest.getNumThreads()).orElse(ecaLoadTestsConfig.getNumThreads());
        LoadTestEntity loadTestEntity = new LoadTestEntity();
        loadTestEntity.setTestUuid(UUID.randomUUID().toString());
        loadTestEntity.setNumRequests(numRequests);
        loadTestEntity.setNumThreads(numThreads);
        loadTestEntity.setExecutionStatus(ExecutionStatus.NEW);
        loadTestEntity.setCreated(LocalDateTime.now());
        return loadTestRepository.save(loadTestEntity);
    }
}
