package com.ecaservice.auto.test.service.step;

import com.ecaservice.auto.test.entity.autotest.EvaluationRequestEntity;
import com.ecaservice.auto.test.event.model.EvaluationResultsTestStepEvent;
import com.ecaservice.auto.test.service.ErsService;
import com.ecaservice.auto.test.service.EvaluationResultsMatcherService;
import com.ecaservice.test.common.service.TestResultsMatcher;
import eca.core.ModelSerializationHelper;
import eca.core.evaluation.EvaluationResults;
import eca.core.model.ClassificationModel;
import eca.data.file.resource.UrlResource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;

/**
 * Evaluation results test step handler.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
public class EvaluationResultsTestStepHandler implements AbstractTestStepHandler<EvaluationResultsTestStepEvent> {

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
    public EvaluationResultsTestStepHandler(ErsService ersService,
                                            EvaluationResultsMatcherService evaluationResultsMatcherService,
                                            TestStepService testStepService) {
        this.ersService = ersService;
        this.evaluationResultsMatcherService = evaluationResultsMatcherService;
        this.testStepService = testStepService;
    }

    @Override
    @EventListener
    public void handle(EvaluationResultsTestStepEvent event) {
        var evaluationResultsStep = event.getEvaluationResultsTestStepEntity();
        var evaluationRequestEntity = evaluationResultsStep.getEvaluationRequestEntity();
        log.info("Starting to compare and match evaluation [{}] results", evaluationRequestEntity.getRequestId());
        try {
            ClassificationModel classificationModel = downloadClassifierModel(evaluationRequestEntity);
            TestResultsMatcher matcher = new TestResultsMatcher();
            var evaluationResultsResponse = ersService.getEvaluationResults(evaluationRequestEntity.getRequestId());
            EvaluationResults evaluationResults =
                    new EvaluationResults(classificationModel.getClassifier(), classificationModel.getEvaluation());
            var evaluationResultsDetailsMatch =
                    evaluationResultsMatcherService.compareAndMatch(evaluationResults, evaluationResultsResponse,
                            matcher);
            evaluationResultsStep.setEvaluationResultsDetails(evaluationResultsDetailsMatch);
            testStepService.complete(evaluationResultsStep, matcher);
            log.info("Evaluation [{}] results has been processed", evaluationRequestEntity.getRequestId());
        } catch (Exception ex) {
            log.error("There was an error while process evaluation results [{}] test step [{}]: {}",
                    evaluationRequestEntity.getRequestId(), evaluationResultsStep.getId(), ex.getMessage());
            testStepService.finishWithError(evaluationResultsStep, ex.getMessage());
        }
    }

    private ClassificationModel downloadClassifierModel(EvaluationRequestEntity evaluationRequestEntity)
            throws IOException {
        log.info("Starting to download classifier model [{}]", evaluationRequestEntity.getRequestId());
        URL modelUrl = new URL(evaluationRequestEntity.getDownloadUrl());
        ClassificationModel classificationModel =
                ModelSerializationHelper.deserialize(new UrlResource(modelUrl), ClassificationModel.class);
        log.info("Classifier model [{}] has been downloaded", evaluationRequestEntity.getRequestId());
        return classificationModel;
    }
}
