package com.ecaservice.service.ers;

import com.ecaservice.config.ExperimentConfig;
import com.ecaservice.dto.evaluation.GetEvaluationResultsResponse;
import com.ecaservice.dto.evaluation.ResponseStatus;
import com.ecaservice.mapping.EvaluationLogDetailsMapper;
import com.ecaservice.mapping.ExperimentResultsMapper;
import com.ecaservice.model.entity.ErsResponseStatus;
import com.ecaservice.model.entity.EvaluationLog;
import com.ecaservice.model.entity.EvaluationResultsRequestEntity;
import com.ecaservice.model.entity.Experiment;
import com.ecaservice.model.entity.ExperimentResultsEntity;
import com.ecaservice.model.entity.ExperimentResultsRequest;
import com.ecaservice.model.entity.RequestStatus;
import com.ecaservice.model.experiment.ExperimentResultsRequestSource;
import com.ecaservice.repository.EvaluationResultsRequestEntityRepository;
import com.ecaservice.repository.ExperimentResultsEntityRepository;
import com.ecaservice.repository.ExperimentResultsRequestRepository;
import com.ecaservice.web.dto.model.EnumDto;
import com.ecaservice.web.dto.model.ErsReportDto;
import com.ecaservice.web.dto.model.ErsReportStatus;
import com.ecaservice.web.dto.model.EvaluationLogDetailsDto;
import com.ecaservice.web.dto.model.EvaluationResultsStatus;
import eca.converters.model.ExperimentHistory;
import eca.core.evaluation.EvaluationResults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.ws.client.WebServiceIOException;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
    private final EvaluationLogDetailsMapper evaluationLogDetailsMapper;
    private final ExperimentResultsMapper experimentResultsMapper;
    private final ExperimentResultsRequestRepository experimentResultsRequestRepository;
    private final EvaluationResultsRequestEntityRepository evaluationResultsRequestEntityRepository;
    private final ExperimentResultsEntityRepository experimentResultsEntityRepository;

    /**
     * Constructor with spring dependency injection.
     *
     * @param ersRequestService                        - ers request service bean
     * @param experimentConfig                         - experiment config bean
     * @param evaluationLogDetailsMapper               - evaluation log details mapper bean
     * @param experimentResultsMapper                  - experiment results mapper bean
     * @param experimentResultsRequestRepository       - experiment results request repository bean
     * @param evaluationResultsRequestEntityRepository - evaluation results request repository bean
     * @param experimentResultsEntityRepository        - experiment results entity repository bean
     */
    @Inject
    public ErsService(ErsRequestService ersRequestService,
                      ExperimentConfig experimentConfig,
                      EvaluationLogDetailsMapper evaluationLogDetailsMapper,
                      ExperimentResultsMapper experimentResultsMapper,
                      ExperimentResultsRequestRepository experimentResultsRequestRepository,
                      EvaluationResultsRequestEntityRepository evaluationResultsRequestEntityRepository,
                      ExperimentResultsEntityRepository experimentResultsEntityRepository) {
        this.ersRequestService = ersRequestService;
        this.experimentConfig = experimentConfig;
        this.evaluationLogDetailsMapper = evaluationLogDetailsMapper;
        this.experimentResultsMapper = experimentResultsMapper;
        this.experimentResultsRequestRepository = experimentResultsRequestRepository;
        this.evaluationResultsRequestEntityRepository = evaluationResultsRequestEntityRepository;
        this.experimentResultsEntityRepository = experimentResultsEntityRepository;
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
                    experimentResultsRequest -> ErsResponseStatus.SUCCESS.equals(
                            experimentResultsRequest.getResponseStatus())).count());
            ersReportDto.setFailedRequestsCount(experimentResultsRequests.stream().filter(
                    experimentResultsRequest -> !ErsResponseStatus.SUCCESS.equals(
                            experimentResultsRequest.getResponseStatus())).count());
        }
        populateErsReportStatus(experiment, ersReportDto);
        return ersReportDto;
    }

    /**
     * Gets evaluation log report.
     *
     * @param evaluationLog - evaluation log entity
     * @return evaluation log details dto
     */
    public EvaluationLogDetailsDto getEvaluationLogDetails(EvaluationLog evaluationLog) {
        EvaluationLogDetailsDto evaluationLogDetailsDto = evaluationLogDetailsMapper.map(evaluationLog);
        populateEvaluationResults(evaluationLogDetailsDto, evaluationLog);
        return evaluationLogDetailsDto;
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
        //List<EvaluationResults> evaluationResults = experimentHistory.getExperiment();
        /*int resultsSize = Integer.min(evaluationResults.size(), experimentConfig.getResultSizeToSend());
        evaluationResults.stream().limit(resultsSize).forEach(results -> {
            ExperimentResultsRequest experimentResultsRequest = new ExperimentResultsRequest();
            experimentResultsRequest.setRequestSource(source);
            experimentResultsRequest.setExperiment(experiment);
            ersRequestService.saveEvaluationResults(results, experimentResultsRequest);
        });*/
        List<ExperimentResultsEntity> experimentResultsEntities =
                getOrSaveExperimentResults(experiment, experimentHistory);
        IntStream.range(0, experimentResultsEntities.size()).forEach(i -> {
            ExperimentResultsRequest experimentResultsRequest = new ExperimentResultsRequest();
            experimentResultsRequest.setRequestSource(source);
            ExperimentResultsEntity experimentResultsEntity = experimentResultsEntities.get(i);
            experimentResultsRequest.setExperimentResultsEntity(experimentResultsEntity);
            EvaluationResults evaluationResults =
                    experimentHistory.getExperiment().get(experimentResultsEntity.getResultsIndex());
            ersRequestService.saveEvaluationResults(evaluationResults, experimentResultsRequest);
        });
    }

    private List<ExperimentResultsEntity> getOrSaveExperimentResults(Experiment experiment,
                                                                     ExperimentHistory experimentHistory) {
        List<ExperimentResultsEntity> experimentResultsEntities =
                experimentResultsEntityRepository.findByExperiment(experiment);
        if (CollectionUtils.isEmpty(experimentResultsEntities)) {
            List<EvaluationResults> evaluationResultsList = experimentHistory.getExperiment();
            int resultsSize = Integer.min(evaluationResultsList.size(), experimentConfig.getResultSizeToSend());
            experimentResultsEntities = IntStream.range(0, resultsSize).mapToObj(i -> {
                EvaluationResults evaluationResults = evaluationResultsList.get(i);
                ExperimentResultsEntity experimentResultsEntity =
                        experimentResultsMapper.map(evaluationResults);
                experimentResultsEntity.setExperiment(experiment);
                experimentResultsEntity.setResultsIndex(i);
                return experimentResultsEntity;
            }).collect(Collectors.toList());
            experimentResultsEntityRepository.saveAll(experimentResultsEntities);
        }
        return experimentResultsEntities;
    }

    private void populateErsReportStatus(Experiment experiment, ErsReportDto ersReportDto) {
        ErsReportStatus ersReportStatus;
        if (!RequestStatus.FINISHED.equals(experiment.getExperimentStatus())) {
            ersReportStatus = RequestStatus.NEW.equals(experiment.getExperimentStatus()) ?
                    ErsReportStatus.EXPERIMENT_IN_PROGRESS : ErsReportStatus.EXPERIMENT_ERROR;
            //else handle ERS report status for experiment with FINISHED status
        } else if (ersReportDto.getSuccessfullySavedClassifiers() > 0) {
            ersReportStatus = ErsReportStatus.SUCCESS_SENT;
        } else if (experiment.getDeletedDate() != null) {
            ersReportStatus = ErsReportStatus.EXPERIMENT_DELETED;
        } else {
            ersReportStatus = ErsReportStatus.NEED_SENT;
        }
        ersReportDto.setErsReportStatus(new EnumDto(ersReportStatus.name(), ersReportStatus.getDescription()));
    }

    private void populateEvaluationResults(EvaluationLogDetailsDto evaluationLogDetailsDto,
                                           EvaluationLog evaluationLog) {
        EvaluationResultsStatus evaluationResultsStatus;
        if (!RequestStatus.FINISHED.equals(evaluationLog.getEvaluationStatus())) {
            evaluationResultsStatus = RequestStatus.NEW.equals(evaluationLog.getEvaluationStatus()) ?
                    EvaluationResultsStatus.EVALUATION_IN_PROGRESS : EvaluationResultsStatus.EVALUATION_ERROR;
        } else {
            EvaluationResultsRequestEntity evaluationResultsRequestEntity =
                    evaluationResultsRequestEntityRepository.findByEvaluationLog(evaluationLog);
            if (evaluationResultsRequestEntity == null ||
                    !ErsResponseStatus.SUCCESS.equals(evaluationResultsRequestEntity.getResponseStatus())) {
                evaluationResultsStatus = EvaluationResultsStatus.RESULTS_NOT_SENT;
            } else {
                try {
                    GetEvaluationResultsResponse evaluationResultsResponse =
                            ersRequestService.getEvaluationResults(evaluationResultsRequestEntity.getRequestId());
                    if (ResponseStatus.SUCCESS.equals(evaluationResultsResponse.getStatus())) {
                        evaluationLogDetailsMapper.update(evaluationResultsResponse, evaluationLogDetailsDto);
                    }
                    evaluationResultsStatus = handleEvaluationResultsStatus(evaluationResultsResponse);
                } catch (WebServiceIOException ex) {
                    log.error(ex.getMessage());
                    evaluationResultsStatus = EvaluationResultsStatus.ERS_SERVICE_UNAVAILABLE;
                } catch (Exception ex) {
                    log.error("There was an error while fetching evaluation results for evaluation log [{}]: {}",
                            evaluationLog.getRequestId(), ex.getMessage());
                    evaluationResultsStatus = EvaluationResultsStatus.ERROR;
                }
            }
        }
        evaluationLogDetailsDto.setEvaluationResultsStatus(
                new EnumDto(evaluationResultsStatus.name(), evaluationResultsStatus.getDescription()));
    }

    private EvaluationResultsStatus handleEvaluationResultsStatus(GetEvaluationResultsResponse response) {
        if (ResponseStatus.SUCCESS.equals(response.getStatus())) {
            return EvaluationResultsStatus.RESULTS_RECEIVED;
        } else if (ResponseStatus.RESULTS_NOT_FOUND.equals(response.getStatus())) {
            return EvaluationResultsStatus.EVALUATION_RESULTS_NOT_FOUND;
        } else {
            return EvaluationResultsStatus.ERROR;
        }
    }
}
