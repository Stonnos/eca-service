package com.ecaservice.service.ers;

import com.ecaservice.config.ExperimentConfig;
import com.ecaservice.dto.evaluation.ResponseStatus;
import com.ecaservice.model.entity.Experiment;
import com.ecaservice.model.entity.ExperimentResultsRequest;
import com.ecaservice.model.entity.RequestStatus;
import com.ecaservice.model.experiment.ExperimentResultsRequestSource;
import com.ecaservice.repository.ExperimentResultsRequestRepository;
import com.ecaservice.web.dto.model.ErsReportDto;
import com.ecaservice.web.dto.model.ErsReportStatus;
import eca.converters.model.ExperimentHistory;
import eca.core.evaluation.EvaluationResults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.inject.Inject;
import java.util.List;

/**
 * ERS service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
public class ErsService {

    private final ErsRequestService ersRequestService;
    private final ExperimentConfig experimentConfig;
    private final ExperimentResultsRequestRepository experimentResultsRequestRepository;

    /**
     * Constructor with spring dependency injection.
     *
     * @param ersRequestService                  - ers request service bean
     * @param experimentConfig                   - experiment config bean
     * @param experimentResultsRequestRepository - experiment results requests repository bean
     */
    @Inject
    public ErsService(ErsRequestService ersRequestService,
                      ExperimentConfig experimentConfig,
                      ExperimentResultsRequestRepository experimentResultsRequestRepository) {
        this.ersRequestService = ersRequestService;
        this.experimentConfig = experimentConfig;
        this.experimentResultsRequestRepository = experimentResultsRequestRepository;
    }

    /**
     * Gets ERS report for specified experiment.
     *
     * @param experiment - experiment entity
     * @return ERS report dto
     */
    public ErsReportDto getErsReport(Experiment experiment) {
        List<ExperimentResultsRequest> experimentResultsRequests =
                experimentResultsRequestRepository.findAllByExperiment(experiment);
        ErsReportDto ersReportDto = new ErsReportDto();
        ersReportDto.setExperimentUuid(experiment.getUuid());
        if (!CollectionUtils.isEmpty(experimentResultsRequests)) {
            ersReportDto.setRequestsCount(experimentResultsRequests.size());
            ersReportDto.setSuccessfullySavedClassifiers(experimentResultsRequests.stream().filter(
                    experimentResultsRequest -> ResponseStatus.SUCCESS.equals(
                            experimentResultsRequest.getResponseStatus())).count());
            ersReportDto.setFailedRequestsCount(experimentResultsRequests.stream().filter(
                    experimentResultsRequest -> !ResponseStatus.SUCCESS.equals(
                            experimentResultsRequest.getResponseStatus())).count());
        }
        setErsReportStatus(experiment, ersReportDto);
        return ersReportDto;
    }

    /**
     * Sent experiment history to ERS service.
     *
     * @param experiment        - experiment entity
     * @param experimentHistory - experiment history
     * @param source            - experiment results request source
     */
    public void sentExperimentHistory(Experiment experiment, ExperimentHistory experimentHistory,
                                      ExperimentResultsRequestSource source) {
        List<EvaluationResults> evaluationResults = experimentHistory.getExperiment();
        int resultsSize = Integer.min(evaluationResults.size(), experimentConfig.getResultSizeToSend());
        evaluationResults.stream().limit(resultsSize).forEach(results -> {
            ExperimentResultsRequest experimentResultsRequest = new ExperimentResultsRequest();
            experimentResultsRequest.setRequestSource(source);
            experimentResultsRequest.setExperiment(experiment);
            ersRequestService.saveEvaluationResults(results, experimentResultsRequest);
        });
    }

    private void setErsReportStatus(Experiment experiment, ErsReportDto ersReportDto) {
        ErsReportStatus ersReportStatus;
        if (!RequestStatus.FINISHED.equals(experiment.getExperimentStatus())) {
            ersReportStatus = ErsReportStatus.EXPERIMENT_ERROR;
        } else if (ersReportDto.getSuccessfullySavedClassifiers() > 0) {
            ersReportStatus = ErsReportStatus.SUCCESS_SENT;
        } else if (experiment.getDeletedDate() != null) {
            ersReportStatus = ErsReportStatus.EXPERIMENT_DELETED;
        } else {
            ersReportStatus = ErsReportStatus.NEED_SENT;
        }
        ersReportDto.setErsReportStatus(ersReportStatus);
    }
}
