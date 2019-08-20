package com.ecaservice.service.experiment;

import com.ecaservice.dto.evaluation.GetEvaluationResultsResponse;
import com.ecaservice.dto.evaluation.ResponseStatus;
import com.ecaservice.dto.evaluation.StatisticsReport;
import com.ecaservice.model.entity.Experiment;
import com.ecaservice.model.entity.ExperimentResultsEntity;
import com.ecaservice.model.entity.ExperimentResultsRequest;
import com.ecaservice.repository.ExperimentRepository;
import com.ecaservice.repository.ExperimentResultsEntityRepository;
import com.ecaservice.repository.ExperimentResultsRequestRepository;
import com.ecaservice.service.ers.ErsRequestService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.IntStream;

import static com.google.common.collect.Lists.newArrayList;

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

    @Data
    @AllArgsConstructor
    private static class ExperimentResultsWrapper {
        ExperimentResultsRequest experimentResultsRequest;
        GetEvaluationResultsResponse evaluationResultsResponse;
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
                List<ExperimentResultsWrapper> experimentResultsWrappers =
                        getExperimentResultsFromErs(experimentResultsRequests);
                //Sorts results
                experimentResultsWrappers.sort((o1, o2) -> {
                    StatisticsReport statisticsReport1 = o1.getEvaluationResultsResponse().getStatistics();
                    StatisticsReport statisticsReport2 = o2.getEvaluationResultsResponse().getStatistics();
                    int compare = -statisticsReport1.getPctCorrect().compareTo(statisticsReport2.getPctCorrect());
                    return compare == 0 ?
                            -statisticsReport1.getMaxAucValue().compareTo(statisticsReport2.getMaxAucValue()) : compare;
                });

                IntStream.range(0, experimentResultsWrappers.size()).forEach(i -> {
                    saveExperimentDetails(experiment, experimentResultsWrappers.get(i), i);
                });
            }
        }
    }

    private List<ExperimentResultsWrapper> getExperimentResultsFromErs(
            List<ExperimentResultsRequest> experimentResultsRequests) {
        List<ExperimentResultsWrapper> experimentResultsWrappers = newArrayList();
        for (ExperimentResultsRequest experimentResultsRequest : experimentResultsRequests) {
            try {
                GetEvaluationResultsResponse evaluationResultsResponse =
                        ersRequestService.getEvaluationResults(experimentResultsRequest.getRequestId());
                if (!ResponseStatus.SUCCESS.equals(evaluationResultsResponse.getStatus())) {
                    log.warn("Evaluation results not found for results request {}",
                            experimentResultsRequest.getRequestId());
                } else {
                    log.info("Fetched evaluation results for results request {}",
                            experimentResultsRequest.getRequestId());
                    experimentResultsWrappers.add(new ExperimentResultsWrapper(experimentResultsRequest,
                            evaluationResultsResponse));
                }
            } catch (Exception ex) {
                log.error("There was an error while fetching evaluation results for request {}",
                        experimentResultsRequest.getRequestId());
            }
        }
        return experimentResultsWrappers;
    }

    private void saveExperimentDetails(Experiment experiment, ExperimentResultsWrapper experimentResultsWrapper,
                                       int resultsIndex) {
        ExperimentResultsEntity experimentResultsEntity = new ExperimentResultsEntity();
        GetEvaluationResultsResponse evaluationResultsResponse = experimentResultsWrapper
                .getEvaluationResultsResponse();
        experimentResultsEntity.setClassifierName(evaluationResultsResponse.getClassifierReport().getClassifierName());
        experimentResultsEntity.setPctCorrect(evaluationResultsResponse.getStatistics().getPctCorrect());
        experimentResultsEntity.setExperiment(experiment);
        experimentResultsEntity.setResultsIndex(resultsIndex);
        experimentResultsEntityRepository.save(experimentResultsEntity);
        ExperimentResultsRequest experimentResultsRequest = experimentResultsWrapper.getExperimentResultsRequest();
        experimentResultsRequest.setExperimentResultsEntity(experimentResultsEntity);
        experimentResultsRequestRepository.save(experimentResultsRequest);
        log.info("Migrated experiment details for results request {}", experimentResultsRequest.getRequestId());
    }
}
