package com.ecaservice.server.report;

import com.ecaservice.ers.dto.GetEvaluationResultsResponse;
import com.ecaservice.server.mapping.EvaluationResultsReportDataMapper;
import com.ecaservice.server.model.entity.AbstractEvaluationEntity;
import com.ecaservice.server.model.evaluation.ConfusionMatrixData;
import com.ecaservice.server.report.model.EvaluationResultsReportBean;
import com.ecaservice.server.report.model.EvaluationResultsReportInputData;
import com.ecaservice.server.service.classifiers.ClassifierOptionsInfoProcessor;
import com.ecaservice.server.service.evaluation.ConfusionMatrixService;
import com.ecaservice.web.dto.model.ClassifierInfoDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Evaluation results report data fetcher.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EvaluationResultsReportDataProcessor {

    private final EvaluationResultsReportDataMapper evaluationResultsReportDataMapper;
    private final ClassifierOptionsInfoProcessor classifierOptionsInfoProcessor;
    private final ConfusionMatrixService confusionMatrixService;

    /**
     * Processes evaluation results report data.
     *
     * @param inputData - evaluation results report input data
     * @return evaluation results report data
     */
    public EvaluationResultsReportBean processReportData(EvaluationResultsReportInputData inputData) {
        AbstractEvaluationEntity evaluationEntity = inputData.getEvaluationEntity();
        GetEvaluationResultsResponse evaluationResultsResponse = inputData.getEvaluationResultsResponse();
        log.info("Starting to process evaluation results report data for evaluation [{}], ers results id [{}]",
                evaluationEntity.getRequestId(), evaluationResultsResponse.getRequestId());
        EvaluationResultsReportBean evaluationResultsReportBean =
                evaluationResultsReportDataMapper.map(evaluationEntity, evaluationResultsResponse);
        ClassifierInfoDto classifierInfoDto =
                classifierOptionsInfoProcessor.processClassifierInfo(inputData.getClassifierOptions());
        evaluationResultsReportBean.setClassifierInfo(classifierInfoDto);
        ConfusionMatrixData confusionMatrixData =
                confusionMatrixService.proceedConfusionMatrix(evaluationResultsResponse);
        evaluationResultsReportBean.setConfusionMatrixCells(confusionMatrixData.getConfusionMatrixCells());
        log.info("Evaluation results report data has been processed for evaluation [{}], ers results id [{}]",
                evaluationEntity.getRequestId(), evaluationResultsResponse.getRequestId());
        return evaluationResultsReportBean;
    }
}
