package com.ecaservice.service.experiment;

import com.ecaservice.dto.evaluation.GetEvaluationResultsResponse;
import com.ecaservice.dto.evaluation.ResponseStatus;
import com.ecaservice.model.entity.Experiment;
import com.ecaservice.model.entity.ExperimentResultsEntity;
import com.ecaservice.model.entity.ExperimentResultsRequest;
import com.ecaservice.repository.ExperimentResultsEntityRepository;
import com.ecaservice.repository.ExperimentRepository;
import com.ecaservice.repository.ExperimentResultsRequestRepository;
import com.ecaservice.service.ers.ErsRequestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.inject.Inject;
import java.util.List;

/**
 * @author Roman Batygin
 */
@Slf4j
@Service
public class ExperimentResultsMigrationService {

    private final ExperimentRepository experimentRepository;
    private final ExperimentResultsRequestRepository experimentResultsRequestRepository;
    private final ExperimentResultsEntityRepository experimentResultsEntityRepository;
    private final ErsRequestService ersRequestService;

    @Inject
    public ExperimentResultsMigrationService(ExperimentRepository experimentRepository,
                                             ExperimentResultsRequestRepository experimentResultsRequestRepository,
                                             ExperimentResultsEntityRepository experimentResultsEntityRepository,
                                             ErsRequestService ersRequestService) {
        this.experimentRepository = experimentRepository;
        this.experimentResultsRequestRepository = experimentResultsRequestRepository;
        this.experimentResultsEntityRepository = experimentResultsEntityRepository;
        this.ersRequestService = ersRequestService;
    }

    public void migrate() {
        List<Experiment> experiments = experimentRepository.findAll();
        for (Experiment experiment : experiments) {
            List<ExperimentResultsRequest> experimentResultsRequests =
                    experimentResultsRequestRepository.findAllByExperiment(experiment);
            if (CollectionUtils.isEmpty(experimentResultsRequests)) {
                log.info("No one results requests found for experiment {}", experiment.getUuid());
            } else {
                log.info("Found {} results requests for experiment {}", experimentResultsRequests.size(),
                        experiment.getUuid());
                for (ExperimentResultsRequest experimentResultsRequest : experimentResultsRequests) {
                    GetEvaluationResultsResponse evaluationResultsResponse =
                            ersRequestService.getEvaluationResults(experimentResultsRequest.getRequestId());
                    if (!ResponseStatus.SUCCESS.equals(evaluationResultsResponse.getStatus())) {
                        log.warn("Evaluation results not found for results request {}",
                                experimentResultsRequest.getRequestId());
                    } else {
                        log.info("Fetched evaluation results for results request {}",
                                experimentResultsRequest.getRequestId());
                        saveExperimentDetails(experiment, experimentResultsRequest, evaluationResultsResponse);
                        log.info("Migrated experiment details for results request {}",
                                experimentResultsRequest.getRequestId());
                    }
                }
            }
        }
    }

    private void saveExperimentDetails(Experiment experiment, ExperimentResultsRequest experimentResultsRequest,
                                       GetEvaluationResultsResponse evaluationResultsResponse) {
        ExperimentResultsEntity experimentResultsEntity = new ExperimentResultsEntity();
        experimentResultsEntity.setClassifierName(evaluationResultsResponse.getClassifierReport().getClassifierName());
        experimentResultsEntity.setPctCorrect(evaluationResultsResponse.getStatistics().getPctCorrect());
        experimentResultsEntity.setExperiment(experiment);
        experimentResultsRequest.setExperimentResultsEntity(experimentResultsEntity);
        experimentResultsEntityRepository.save(experimentResultsEntity);
        experimentResultsRequestRepository.save(experimentResultsRequest);
    }
}
