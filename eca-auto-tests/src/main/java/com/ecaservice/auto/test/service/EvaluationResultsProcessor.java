package com.ecaservice.auto.test.service;

import com.ecaservice.auto.test.entity.autotest.BaseEvaluationRequestEntity;
import com.ecaservice.auto.test.entity.autotest.EvaluationRequestEntity;
import com.ecaservice.auto.test.entity.autotest.ExperimentRequestEntity;
import com.ecaservice.auto.test.entity.autotest.RequestStageType;
import com.ecaservice.auto.test.model.evaluation.EvaluationResultsDetailsMatch;
import com.ecaservice.auto.test.repository.autotest.BaseEvaluationRequestRepository;
import com.ecaservice.auto.test.repository.autotest.BaseTestStepRepository;
import com.ecaservice.auto.test.service.api.EcaServerClient;
import com.ecaservice.ers.dto.GetEvaluationResultsResponse;
import com.ecaservice.test.common.model.ExecutionStatus;
import com.ecaservice.test.common.service.TestResultsMatcher;
import eca.core.evaluation.EvaluationResults;
import eca.dataminer.AbstractExperiment;
import lombok.Cleanup;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static com.ecaservice.test.common.util.Utils.calculateTestResult;
import static com.google.common.collect.Lists.newArrayList;

/**
 * Evaluation results processor service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EvaluationResultsProcessor {

    private static final String SLASH_SEPARATOR = "/";

    private final EcaServerClient ecaServerClient;
    private final EvaluationRequestService evaluationRequestService;
    private final ErsService ersService;
    private final EvaluationResultsMatcherService evaluationResultsMatcherService;
    private final BaseEvaluationRequestRepository baseEvaluationRequestRepository;
    private final BaseTestStepRepository baseTestStepRepository;

    /**
     * Compares and matches experiment results.
     *
     * @param evaluationRequestEntity - experiment request entity
     */
    public void compareAndMatchEvaluationResults(EvaluationRequestEntity evaluationRequestEntity,
                                                 EvaluationResults evaluationResults) {
        log.info("Starting to compare and match evaluation [{}] results", evaluationRequestEntity.getRequestId());
        try {
            TestResultsMatcher matcher = new TestResultsMatcher();
            var evaluationResultsResponse = ersService.getEvaluationResults(evaluationRequestEntity.getRequestId());
            var evaluationResultsDetailsMatch =
                    evaluationResultsMatcherService.compareAndMatch(evaluationResults, evaluationResultsResponse,
                            matcher);
            evaluationRequestEntity.setEvaluationResultsDetails(evaluationResultsDetailsMatch);
            populateAndSaveFinalTestResults(evaluationRequestEntity, matcher);
            log.info("Got evaluation request [{}] test result: {}",
                    evaluationRequestEntity.getRequestId(), evaluationRequestEntity.getTestResult());
            log.info("Evaluation [{}] results has been processed", evaluationRequestEntity.getRequestId());
        } catch (Exception ex) {
            log.error("There was an error while process evaluation results [{}]: {}",
                    evaluationRequestEntity.getRequestId(), ex.getMessage());
            evaluationRequestService.finishWithError(evaluationRequestEntity, ex.getMessage());
        }
    }

    /**
     * Compares and matches experiment results.
     *
     * @param experimentRequestEntity - experiment request entity
     */
    public void compareAndMatchExperimentResults(ExperimentRequestEntity experimentRequestEntity) {
        log.info("Starting to compare and match experiment [{}] results", experimentRequestEntity.getRequestId());
        try {
            TestResultsMatcher matcher = new TestResultsMatcher();
            AbstractExperiment<?> experimentHistory = downloadExperimentHistory(experimentRequestEntity);
            var experimentEvaluationResults =
                    ersService.getExperimentEvaluationResults(experimentRequestEntity.getRequestId());
            var evaluationResultsDetailsMatches =
                    compareAndMatchResults(experimentRequestEntity, experimentHistory, experimentEvaluationResults,
                            matcher);
            experimentRequestEntity.setExperimentResultDetails(evaluationResultsDetailsMatches);
            populateAndSaveFinalTestResults(experimentRequestEntity, matcher);
            log.info("Got experiment request [{}] test result: {}",
                    experimentRequestEntity.getRequestId(), experimentRequestEntity.getTestResult());
            log.info("Experiment [{}] results has been processed", experimentRequestEntity.getRequestId());
        } catch (Exception ex) {
            log.error("There was an error while process experiment results [{}]: {}",
                    experimentRequestEntity.getRequestId(), ex.getMessage());
            evaluationRequestService.finishWithError(experimentRequestEntity, ex.getMessage());
        }
    }

    private void populateAndSaveFinalTestResults(BaseEvaluationRequestEntity requestEntity,
                                                 TestResultsMatcher matcher) {
        requestEntity.setTotalMatched(matcher.getTotalMatched());
        requestEntity.setTotalNotMatched(matcher.getTotalNotMatched());
        requestEntity.setTotalNotFound(matcher.getTotalNotFound());
        requestEntity.setTestResult(calculateTestResult(matcher));
        requestEntity.setStageType(RequestStageType.COMPLETED);
        if (baseTestStepRepository.existsByEvaluationRequestEntity(requestEntity)) {
            log.info("Request [{}] has additional test steps. Wait for them to complete", requestEntity.getRequestId());
        } else {
            requestEntity.setExecutionStatus(ExecutionStatus.FINISHED);
            requestEntity.setFinished(LocalDateTime.now());
            log.info("Request [{}] has no one additional test step. Just finish execution",
                    requestEntity.getRequestId());
        }
        baseEvaluationRequestRepository.save(requestEntity);
    }

    private List<EvaluationResultsDetailsMatch> compareAndMatchResults(ExperimentRequestEntity experimentRequestEntity,
                                                                       AbstractExperiment<?> experimentHistory,
                                                                       Map<Integer, GetEvaluationResultsResponse> evaluationResultsMap,
                                                                       TestResultsMatcher matcher) {
        List<EvaluationResultsDetailsMatch> evaluationResultsDetailsMatches = newArrayList();
        IntStream.range(0, experimentHistory.getHistory().size()).forEach(i -> {
            var evaluationResults = experimentHistory.getHistory().get(i);
            var evaluationResultsResponse = evaluationResultsMap.get(i);
            if (evaluationResultsResponse == null) {
                throw new IllegalStateException(
                        String.format("ERS response not found for experiment [%s] results with index [%d]",
                                experimentRequestEntity.getRequestId(), i));
            }
            var evaluationResultsDetailsMatch =
                    evaluationResultsMatcherService.compareAndMatch(evaluationResults, evaluationResultsResponse,
                            matcher);
            evaluationResultsDetailsMatch.setResultIndex(i);
            evaluationResultsDetailsMatches.add(evaluationResultsDetailsMatch);
        });
        return evaluationResultsDetailsMatches;
    }

    private AbstractExperiment<?> downloadExperimentHistory(ExperimentRequestEntity experimentRequestEntity)
            throws IOException {
        log.info("Starting to download experiment [{}] history",
                experimentRequestEntity.getRequestId());
        String token = StringUtils.substringAfterLast(experimentRequestEntity.getDownloadUrl(),
                SLASH_SEPARATOR);
        Resource modelResource = ecaServerClient.downloadModel(token);
        @Cleanup InputStream inputStream = modelResource.getInputStream();
        AbstractExperiment<?> experimentHistory = SerializationUtils.deserialize(inputStream);
        log.info("Experiment [{}] history has been downloaded", experimentRequestEntity.getRequestId());
        return experimentHistory;
    }
}
