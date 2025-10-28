package com.ecaservice.server.report;

import com.ecaservice.common.web.exception.EntityNotFoundException;
import com.ecaservice.ers.dto.GetEvaluationResultsResponse;
import com.ecaservice.server.model.entity.ErsResponseStatus;
import com.ecaservice.server.model.entity.EvaluationResultsAttachmentType;
import com.ecaservice.server.model.entity.EvaluationResultsRequestEntity;
import com.ecaservice.server.model.entity.Experiment;
import com.ecaservice.server.model.entity.ExperimentResultsEntity;
import com.ecaservice.server.model.entity.ExperimentResultsRequest;
import com.ecaservice.server.report.model.EvaluationResultsReportBean;
import com.ecaservice.server.report.model.EvaluationResultsReportInputData;
import com.ecaservice.server.repository.ExperimentResultsEntityRepository;
import com.ecaservice.server.repository.ExperimentResultsRequestRepository;
import com.ecaservice.server.service.EvaluationResultsAttachmentService;
import com.ecaservice.server.service.ers.ErsRequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.ecaservice.server.util.EvaluationResultsAttachmentKeys.EXPERIMENT_RESULTS_KEY_FORMAT;
import static com.ecaservice.server.util.ValidationUtils.checkFinishedRequestStatus;

/**
 * Experiment results report data fetcher.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ExperimentResultsReportDataFetcher implements EvaluationResultsReportDataFetcher {

    private final EvaluationResultsReportDataProcessor evaluationResultsReportDataProcessor;
    private final ErsRequestService ersRequestService;
    private final EvaluationResultsAttachmentService evaluationResultsAttachmentService;
    private final ExperimentResultsEntityRepository experimentResultsEntityRepository;
    private final ExperimentResultsRequestRepository experimentResultsRequestRepository;

    /**
     * Gets experiment results report data.
     *
     * @param id - experiment results id
     * @return experiment results report data
     */
    @Override
    public EvaluationResultsReportBean getReportData(long id) {
        log.info("Starting to fetch experiment result report data for evaluation log [{}]", id);
        ExperimentResultsEntity experimentResultsEntity = experimentResultsEntityRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ExperimentResultsEntity.class, id));
        Experiment experiment = experimentResultsEntity.getExperiment();
        checkFinishedRequestStatus(experiment);
        ExperimentResultsRequest experimentResultsRequest =
                experimentResultsRequestRepository.findByExperimentResults(experimentResultsEntity);
        if (experimentResultsRequest == null ||
                !ErsResponseStatus.SUCCESS.equals(experimentResultsRequest.getResponseStatus())) {
            throw new EntityNotFoundException(EvaluationResultsRequestEntity.class, experimentResultsEntity.getId());
        } else {
            GetEvaluationResultsResponse evaluationResultsResponse =
                    ersRequestService.getEvaluationResults(experimentResultsRequest.getRequestId());
            String rocAttachmentKey = String.format(EXPERIMENT_RESULTS_KEY_FORMAT, experiment.getRequestId(),
                    experimentResultsEntity.getId());
            byte[] rocImage = evaluationResultsAttachmentService.getAttachment(rocAttachmentKey,
                    EvaluationResultsAttachmentType.ROC_CURVE_IMAGE);
            EvaluationResultsReportInputData evaluationResultsReportInputData =
                    EvaluationResultsReportInputData.builder()
                            .evaluationEntity(experimentResultsEntity.getExperiment())
                            .classifierOptions(experimentResultsEntity.getClassifierOptions())
                            .evaluationResultsResponse(evaluationResultsResponse)
                            .rocImage(rocImage)
                            .build();
            EvaluationResultsReportBean evaluationResultsReportBean =
                    evaluationResultsReportDataProcessor.processReportData(evaluationResultsReportInputData);
            log.info("Experiment result report data has been fetched for evaluation log [{}]", id);
            return evaluationResultsReportBean;
        }
    }
}
