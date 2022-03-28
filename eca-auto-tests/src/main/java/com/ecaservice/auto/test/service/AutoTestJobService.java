package com.ecaservice.auto.test.service;

import com.ecaservice.auto.test.dto.AutoTestsJobDto;
import com.ecaservice.auto.test.dto.BaseEvaluationRequestDto;
import com.ecaservice.auto.test.entity.autotest.AutoTestType;
import com.ecaservice.auto.test.entity.autotest.AutoTestsJobEntity;
import com.ecaservice.auto.test.entity.autotest.BaseEvaluationRequestEntity;
import com.ecaservice.auto.test.entity.autotest.TestFeature;
import com.ecaservice.auto.test.entity.autotest.TestFeatureEntity;
import com.ecaservice.auto.test.mapping.AutoTestsMapper;
import com.ecaservice.auto.test.mapping.BaseEvaluationRequestMapper;
import com.ecaservice.auto.test.repository.autotest.AutoTestsJobRepository;
import com.ecaservice.auto.test.repository.autotest.BaseEvaluationRequestRepository;
import com.ecaservice.auto.test.repository.autotest.TestFeatureRepository;
import com.ecaservice.common.web.exception.EntityNotFoundException;
import com.ecaservice.test.common.model.ExecutionStatus;
import com.ecaservice.test.common.report.TestResultsCounter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.ecaservice.test.common.util.Utils.totalTime;

/**
 * Auto test service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AutoTestJobService {

    private final AutoTestsMapper autoTestsMapper;
    private final List<BaseEvaluationRequestMapper> evaluationRequestMappers;
    private final AutoTestsJobRepository autoTestsJobRepository;
    private final BaseEvaluationRequestRepository baseEvaluationRequestRepository;
    private final TestFeatureRepository testFeatureRepository;

    /**
     * Creates experiment auto tests job.
     *
     * @param autoTestType - auto test type
     * @param features     - features
     * @return auto tests job dto
     */
    @Transactional
    public AutoTestsJobDto createAutoTestsJob(AutoTestType autoTestType, List<TestFeature> features) {
        var autoTestsJobEntity = new AutoTestsJobEntity();
        autoTestsJobEntity.setJobUuid(UUID.randomUUID().toString());
        autoTestsJobEntity.setAutoTestType(autoTestType);
        autoTestsJobEntity.setExecutionStatus(ExecutionStatus.NEW);
        autoTestsJobEntity.setCreated(LocalDateTime.now());
        autoTestsJobRepository.save(autoTestsJobEntity);
        saveFeatures(autoTestsJobEntity, features);
        return autoTestsMapper.map(autoTestsJobEntity);
    }

    /**
     * Finds auto tests job by uuid.
     *
     * @param jobUuid - job uuid
     * @return auto tests job entity
     */
    public AutoTestsJobEntity getJob(String jobUuid) {
        return autoTestsJobRepository.findByJobUuid(jobUuid)
                .orElseThrow(() -> new EntityNotFoundException(AutoTestsJobEntity.class, jobUuid));
    }

    /**
     * Gets auto tests job details.
     *
     * @param jobUuid - job uuid
     * @return auto tests job dto
     */
    public AutoTestsJobDto getJobDetails(String jobUuid) {
        log.info("Starting to get auto tests [{}] job details", jobUuid);
        var autoTestsJobEntity = getJob(jobUuid);
        var autoTestsJobDto = autoTestsMapper.map(autoTestsJobEntity);
        var counter = new TestResultsCounter();
        var evaluationRequests = getEvaluationRequests(autoTestsJobEntity, counter);
        autoTestsJobDto.setRequests(evaluationRequests);
        autoTestsJobDto.setSuccess(counter.getPassed());
        autoTestsJobDto.setFailed(counter.getFailed());
        autoTestsJobDto.setErrors(counter.getErrors());
        autoTestsJobDto.setTotalTime(totalTime(autoTestsJobDto.getStarted(), autoTestsJobDto.getFinished()));
        log.info("Fetched auto tests [{}] job details", jobUuid);
        return autoTestsJobDto;
    }

    /**
     * Success completes auto test job.
     *
     * @param autoTestsJobEntity - auto tests job
     */
    public void finish(AutoTestsJobEntity autoTestsJobEntity) {
        autoTestsJobEntity.setExecutionStatus(ExecutionStatus.FINISHED);
        autoTestsJobEntity.setFinished(LocalDateTime.now());
        autoTestsJobRepository.save(autoTestsJobEntity);
        log.info("Auto tests job [{}] has been finished", autoTestsJobEntity.getJobUuid());
    }

    /**
     * Finishes auto tests job with error
     *
     * @param autoTestsJobEntity - auto tests job entity
     * @param errorMessage       - error message
     */
    public void finishWithError(AutoTestsJobEntity autoTestsJobEntity, String errorMessage) {
        autoTestsJobEntity.setDetails(errorMessage);
        autoTestsJobEntity.setExecutionStatus(ExecutionStatus.ERROR);
        autoTestsJobEntity.setFinished(LocalDateTime.now());
        autoTestsJobRepository.save(autoTestsJobEntity);
    }

    @SuppressWarnings("unchecked")
    private List<BaseEvaluationRequestDto> getEvaluationRequests(AutoTestsJobEntity autoTestsJobEntity,
                                                                 TestResultsCounter counter) {
        return baseEvaluationRequestRepository.findAllByJob(autoTestsJobEntity)
                .stream()
                .map(request -> {
                    var evaluationRequestDto = getMapper(request).map(request);
                    evaluationRequestDto.getTestResult().apply(counter);
                    return evaluationRequestDto;
                })
                .collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    private BaseEvaluationRequestMapper getMapper(BaseEvaluationRequestEntity evaluationRequestEntity) {
        return evaluationRequestMappers.stream()
                .filter(mapper -> mapper.canMap(evaluationRequestEntity))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(String.format("Can't map [%s] evaluation request",
                        evaluationRequestEntity.getClass().getSimpleName())));
    }

    private void saveFeatures(AutoTestsJobEntity job, List<TestFeature> features) {
        if (!CollectionUtils.isEmpty(features)) {
            var featureEntities = features.stream()
                    .map(feature -> {
                        var testFeatureEntity = new TestFeatureEntity();
                        testFeatureEntity.setTestFeature(feature);
                        testFeatureEntity.setJob(job);
                        return testFeatureEntity;
                    })
                    .collect(Collectors.toList());
            testFeatureRepository.saveAll(featureEntities);
            log.info("[{}] features has been saved for auto tests job [{}]", features, job.getJobUuid());
        }
    }
}
