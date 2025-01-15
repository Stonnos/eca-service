package com.ecaservice.load.test.service;

import com.ecaservice.common.web.exception.EntityNotFoundException;
import com.ecaservice.load.test.config.EcaLoadTestsConfig;
import com.ecaservice.load.test.dto.LoadTestDto;
import com.ecaservice.load.test.dto.LoadTestRequest;
import com.ecaservice.load.test.entity.LoadTestEntity;
import com.ecaservice.load.test.mapping.LoadTestMapper;
import com.ecaservice.load.test.projection.TestResultStatistics;
import com.ecaservice.load.test.repository.EvaluationRequestRepository;
import com.ecaservice.load.test.repository.LoadTestRepository;
import com.ecaservice.test.common.model.ExecutionStatus;
import com.ecaservice.test.common.model.TestResult;
import eca.core.evaluation.EvaluationMethod;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.ecaservice.load.test.util.Utils.tps;
import static com.ecaservice.test.common.util.Utils.totalTime;

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
    private final LoadTestMapper loadTestMapper;
    private final LoadTestRepository loadTestRepository;
    private final EvaluationRequestRepository evaluationRequestRepository;

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

    /**
     * Gets load test details by uuid.
     *
     * @param testUuid - test uuid
     * @return load test dto
     */
    public LoadTestDto getLoadTestDetails(String testUuid) {
        LoadTestEntity loadTestEntity = getLoadTest(testUuid);
        LoadTestDto loadTestDto = loadTestMapper.mapToDto(loadTestEntity);
        String totalTime = totalTime(loadTestEntity.getStarted(), loadTestEntity.getFinished());
        BigDecimal tps =
                tps(loadTestEntity.getStarted(), loadTestEntity.getFinished(), loadTestEntity.getNumRequests());
        loadTestDto.setTotalTime(totalTime);
        loadTestDto.setTps(tps);
        log.info("Load test [{}] details has been fetched", testUuid);
        return loadTestDto;
    }

    /**
     * Finish load test.
     *
     * @param loadTestEntity - load test entity
     */
    public void finishTest(LoadTestEntity loadTestEntity) {
        log.info("Starting to finish load test [{}]", loadTestEntity.getTestUuid());
        loadTestEntity.setExecutionStatus(ExecutionStatus.FINISHED);
        List<TestResultStatistics> testResultStatistics =
                evaluationRequestRepository.getTestResultStatistics(loadTestEntity);
        Map<TestResult, Integer> testResultStatisticsMap = testResultStatistics.stream()
                .collect(Collectors.toMap(TestResultStatistics::getTestResult,
                        TestResultStatistics::getTestResultCount));
        loadTestEntity.setPassedCount(testResultStatisticsMap.getOrDefault(TestResult.PASSED, 0));
        loadTestEntity.setFailedCount(testResultStatisticsMap.getOrDefault(TestResult.FAILED, 0));
        loadTestEntity.setErrorCount(testResultStatisticsMap.getOrDefault(TestResult.ERROR, 0));
        LocalDateTime finished =
                evaluationRequestRepository.getMaxFinishedDate(loadTestEntity).orElse(LocalDateTime.now());
        loadTestEntity.setFinished(finished);
        loadTestRepository.save(loadTestEntity);
        log.info("Load test [{}] has been finished", loadTestEntity.getTestUuid());
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
