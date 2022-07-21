package com.ecaservice.auto.test.service.step;

import com.ecaservice.auto.test.entity.autotest.ExperimentRequestEntity;
import com.ecaservice.auto.test.event.model.ExperimentResultsTestStepEvent;
import com.ecaservice.auto.test.model.evaluation.EvaluationResultsDetailsMatch;
import com.ecaservice.auto.test.service.ErsService;
import com.ecaservice.auto.test.service.EvaluationResultsMatcherService;
import com.ecaservice.ers.dto.GetEvaluationResultsResponse;
import com.ecaservice.test.common.service.TestResultsMatcher;
import eca.core.ModelSerializationHelper;
import eca.data.file.resource.UrlResource;
import eca.dataminer.AbstractExperiment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static com.google.common.collect.Lists.newArrayList;

/**
 * Experiment results test step handler.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
public class ExperimentResultsTestStepHandler implements AbstractTestStepHandler<ExperimentResultsTestStepEvent> {

    private final ErsService ersService;
    private final EvaluationResultsMatcherService evaluationResultsMatcherService;
    private final TestStepService testStepService;

    /**
     * Constructor with spring dependency injection.
     *
     * @param ersService                      - ers service
     * @param evaluationResultsMatcherService - evaluation results matcher service
     * @param testStepService                 - test step service
     */
    public ExperimentResultsTestStepHandler(ErsService ersService,
                                            EvaluationResultsMatcherService evaluationResultsMatcherService,
                                            TestStepService testStepService) {
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
            log.error("There was an error while process experiment results [{}] test step [{}]: {}",
                    experimentRequestEntity.getRequestId(), experimentResultsStep.getId(), ex.getMessage());
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
        URL experimentUrl = new URL(experimentRequestEntity.getDownloadUrl());
        AbstractExperiment<?> experimentHistory =
                ModelSerializationHelper.deserialize(new UrlResource(experimentUrl), AbstractExperiment.class);
        log.info("Experiment [{}] history has been downloaded", experimentRequestEntity.getRequestId());
        return experimentHistory;
    }
}
