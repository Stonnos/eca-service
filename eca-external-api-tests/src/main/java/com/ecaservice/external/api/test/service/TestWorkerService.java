package com.ecaservice.external.api.test.service;

import com.ecaservice.external.api.dto.EvaluationResponseDto;
import com.ecaservice.external.api.dto.InstancesDto;
import com.ecaservice.external.api.dto.RequestStatus;
import com.ecaservice.external.api.dto.ResponseDto;
import com.ecaservice.external.api.test.config.ExternalApiTestsConfig;
import com.ecaservice.external.api.test.entity.AutoTestEntity;
import com.ecaservice.external.api.test.entity.ExecutionStatus;
import com.ecaservice.external.api.test.entity.TestResult;
import com.ecaservice.external.api.test.exception.EntityNotFoundException;
import com.ecaservice.external.api.test.model.TestTypeVisitor;
import com.ecaservice.external.api.test.model.TestDataModel;
import com.ecaservice.external.api.test.repository.AutoTestRepository;
import com.ecaservice.external.api.test.service.api.ExternalApiClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eca.converters.model.ClassificationModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;

/**
 * Test worker service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TestWorkerService {

    private static final String DOWNLOAD_URL_FORMAT = "%s/download-model/%s";

    private final ExternalApiTestsConfig externalApiTestsConfig;
    private final ExternalApiClient externalApiClient;
    private final ExternalApiService externalApiService;
    private final ObjectMapper objectMapper;
    private final AutoTestRepository autoTestRepository;

    private PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

    /**
     * Executes auto test.
     *
     * @param testId         - test id
     * @param testDataModel  - test data model
     * @param countDownLatch - count down latch
     */
    public void execute(long testId, TestDataModel testDataModel, CountDownLatch countDownLatch) {
        AutoTestEntity autoTestEntity = autoTestRepository.findById(testId).orElseThrow(
                () -> new EntityNotFoundException(AutoTestEntity.class, testId));
        try {
            autoTestEntity.setRequest(objectMapper.writeValueAsString(testDataModel.getRequest()));
            autoTestEntity.setExecutionStatus(ExecutionStatus.IN_PROGRESS);
            autoTestEntity.setStarted(LocalDateTime.now());
            autoTestRepository.save(autoTestEntity);
            internalExecuteTest(autoTestEntity, testDataModel);
        } catch (Exception ex) {
            log.error("Unknown error while auto test [{}] execution: {}", autoTestEntity.getId(), ex.getMessage(), ex);
            finishWithError(autoTestEntity, ex.getMessage());
        } finally {
            autoTestEntity.setFinished(LocalDateTime.now());
            autoTestRepository.save(autoTestEntity);
            countDownLatch.countDown();
        }
    }

    private void internalExecuteTest(AutoTestEntity autoTestEntity, TestDataModel testDataModel) throws Exception {
        TestResultsMatcher matcher = new TestResultsMatcher();
        testDataModel.getTestType().apply(new TestTypeVisitor() {
            @Override
            public void testUsingExternalDataUrl() throws IOException {
                ResponseDto<EvaluationResponseDto> responseDto =
                        processEvaluationRequest(autoTestEntity, testDataModel);
                EvaluationResponseDto evaluationResponseDto = responseDto.getPayload();
                matcher.compareMatchAndReport(responseDto.getRequestStatus(), responseDto.getRequestStatus());
                String expectedUrl = String.format(DOWNLOAD_URL_FORMAT, externalApiTestsConfig.getUrl(),
                        evaluationResponseDto.getRequestId());
                matcher.compareMatchAndReport(expectedUrl, evaluationResponseDto.getModelUrl());
                if (RequestStatus.SUCCESS.equals(responseDto.getRequestStatus())) {
                    log.debug("Starting to download model for test [{}]", autoTestEntity.getId());
                    ClassificationModel classificationModel =
                            externalApiService.downloadModel(responseDto.getPayload().getRequestId());
                    log.debug("Classifier model has been downloaded for test [{}]", autoTestEntity.getId());
                    matcher.compareMatchAndReport(evaluationResponseDto.getNumCorrect().doubleValue(),
                            classificationModel.getEvaluation().correct());
                    matcher.compareMatchAndReport(evaluationResponseDto.getPctCorrect().doubleValue(),
                            classificationModel.getEvaluation().pctCorrect());
                    matcher.compareMatchAndReport(evaluationResponseDto.getMeanAbsoluteError().doubleValue(),
                            classificationModel.getEvaluation().meanAbsoluteError());
                }
                updateFinalTestResult(autoTestEntity, matcher);
            }

            @Override
            public void testUsingApiDataStorage() throws IOException {
                log.debug("Starting to uploads train data [{}] to server for test [{}]",
                        testDataModel.getTrainDataPath(), autoTestEntity.getId());
                Resource resource = resolver.getResource(testDataModel.getTrainDataPath());
                ResponseDto<InstancesDto> instancesDto = externalApiService.uploadInstances(resource);
                log.debug("Train data uploading status [{}] for test [{}]", instancesDto.getRequestStatus(),
                        autoTestEntity.getId());
                if (!RequestStatus.SUCCESS.equals(instancesDto.getRequestStatus())) {
                    finishWithError(autoTestEntity, instancesDto.getErrorDescription());
                } else {
                    updateTrainDataUrl(testDataModel, instancesDto.getPayload(), autoTestEntity);
                    testUsingExternalDataUrl();
                }
            }
        });
    }

    private void updateFinalTestResult(AutoTestEntity autoTestEntity, TestResultsMatcher matcher) {
        if (matcher.getTotalNotMatched() == 0 && matcher.getTotalNotFound() == 0) {
            autoTestEntity.setTestResult(TestResult.PASSED);
        } else {
            autoTestEntity.setTestResult(TestResult.FAILED);
        }
        autoTestEntity.setExecutionStatus(ExecutionStatus.FINISHED);
    }

    private void updateTrainDataUrl(TestDataModel testDataModel, InstancesDto instancesDto,
                                    AutoTestEntity autoTestEntity) throws JsonProcessingException {
        testDataModel.getRequest().setTrainDataUrl(instancesDto.getDataUrl());
        autoTestEntity.setRequest(objectMapper.writeValueAsString(testDataModel.getRequest()));
        autoTestRepository.save(autoTestEntity);
    }

    private ResponseDto<EvaluationResponseDto> processEvaluationRequest(AutoTestEntity autoTestEntity,
                                                                        TestDataModel testDataModel)
            throws JsonProcessingException {
        log.debug("Starting to send evaluation request for test [{}]", autoTestEntity.getId());
        ResponseDto<EvaluationResponseDto> response = externalApiClient.evaluateModel(testDataModel.getRequest());
        log.debug("Received evaluation response for test [{}]", autoTestEntity.getId());
        autoTestEntity.setResponse(objectMapper.writeValueAsString(response));
        autoTestEntity.setRequestStatus(response.getRequestStatus());
        String requestId =
                Optional.ofNullable(response.getPayload()).map(EvaluationResponseDto::getRequestId).orElse(null);
        autoTestEntity.setRequestId(requestId);
        autoTestRepository.save(autoTestEntity);
        return response;
    }

    private void finishWithError(AutoTestEntity autoTestEntity, String errorMessage) {
        autoTestEntity.setTestResult(TestResult.ERROR);
        autoTestEntity.setExecutionStatus(ExecutionStatus.ERROR);
        autoTestEntity.setDetails(errorMessage);
    }
}
