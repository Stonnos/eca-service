package com.ecaservice.auto.test.service.step;

import com.ecaservice.auto.test.entity.autotest.BaseEvaluationRequestEntity;
import com.ecaservice.auto.test.entity.autotest.ExperimentRequestEntity;
import com.ecaservice.auto.test.entity.autotest.RequestStageType;
import com.ecaservice.auto.test.event.model.ExperimentResultsTestStepEvent;
import com.ecaservice.auto.test.model.evaluation.EvaluationResultsDetailsMatch;
import com.ecaservice.auto.test.repository.autotest.BaseEvaluationRequestRepository;
import com.ecaservice.auto.test.service.ErsService;
import com.ecaservice.auto.test.service.EvaluationRequestService;
import com.ecaservice.auto.test.service.EvaluationResultsMatcherService;
import com.ecaservice.auto.test.service.api.EcaServerClient;
import com.ecaservice.ers.dto.GetEvaluationResultsResponse;
import com.ecaservice.test.common.service.TestResultsMatcher;
import eca.dataminer.AbstractExperiment;
import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static com.ecaservice.test.common.util.Utils.calculateTestResult;
import static com.google.common.collect.Lists.newArrayList;

/**
 * Experiment results test step handler.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
public class ExperimentResultsTestStepHandler extends AbstractTestStepHandler<ExperimentResultsTestStepEvent> {

    private static final String SLASH_SEPARATOR = "/";

    private final EcaServerClient ecaServerClient;
    private final ErsService ersService;
    private final EvaluationResultsMatcherService evaluationResultsMatcherService;
    private final TestStepService testStepService;

    /**
     * Constructor with spring dependency injection.
     *
     * @param ecaServerClient                 - eca server client
     * @param ersService                      - ers service
     * @param evaluationResultsMatcherService - evaluation results matcher service
     * @param testStepService                 - test step service
     */
    public ExperimentResultsTestStepHandler(EcaServerClient ecaServerClient,
                                            ErsService ersService,
                                            EvaluationResultsMatcherService evaluationResultsMatcherService,
                                            TestStepService testStepService) {
        super(ExperimentResultsTestStepEvent.class);
        this.ecaServerClient = ecaServerClient;
        this.ersService = ersService;
        this.evaluationResultsMatcherService = evaluationResultsMatcherService;
        this.testStepService = testStepService;
    }

    @Override
    @EventListener
    public void handle(ExperimentResultsTestStepEvent event) {
        var experimentResultsStep = event.getExperimentResultsTestStepEntity();
        var experimentRequestEntity = experimentResultsStep.getEvaluationRequestEntity();
        log.info("Starting to compare and match experiment [{}] results", experimentRequestEntity.getRequestId());
        try {
            TestResultsMatcher matcher = new TestResultsMatcher();
            AbstractExperiment<?> experimentHistory = downloadExperimentHistory(experimentRequestEntity);
            var experimentEvaluationResults =
                    ersService.getExperimentEvaluationResults(experimentRequestEntity.getRequestId());
            var evaluationResultsDetailsMatches =
                    compareAndMatchResults(experimentRequestEntity, experimentHistory, experimentEvaluationResults,
                            matcher);
            experimentResultsStep.setExperimentResultDetails(evaluationResultsDetailsMatches);
            testStepService.complete(experimentResultsStep, matcher);
            log.info("Experiment [{}] results has been processed", experimentRequestEntity.getRequestId());
        } catch (Exception ex) {
            log.error("There was an error while process experiment results [{}]: {}",
                    experimentRequestEntity.getRequestId(), ex.getMessage());
            testStepService.finishWithError(experimentResultsStep, ex.getMessage());
        }
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
