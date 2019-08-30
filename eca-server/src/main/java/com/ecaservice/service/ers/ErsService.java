package com.ecaservice.service.ers;

import com.ecaservice.dto.evaluation.GetEvaluationResultsResponse;
import com.ecaservice.dto.evaluation.ResponseStatus;
import com.ecaservice.mapping.EvaluationLogDetailsMapper;
import com.ecaservice.mapping.ExperimentResultsDetailsMapper;
import com.ecaservice.mapping.ExperimentResultsMapper;
import com.ecaservice.mapping.GetEvaluationResultsMapper;
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
import com.ecaservice.web.dto.model.ErsReportStatus;
import com.ecaservice.web.dto.model.EvaluationLogDetailsDto;
import com.ecaservice.web.dto.model.EvaluationResultsDto;
import com.ecaservice.web.dto.model.EvaluationResultsStatus;
import com.ecaservice.web.dto.model.ExperimentErsReportDto;
import com.ecaservice.web.dto.model.ExperimentResultsDetailsDto;
import eca.converters.model.ExperimentHistory;
import eca.core.evaluation.EvaluationResults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.ws.client.WebServiceIOException;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

import static com.ecaservice.util.Utils.buildEvaluationResultsDto;

/**
 * ERS service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
public class ErsService {

    private final ErsRequestService ersRequestService;
    private final EvaluationLogDetailsMapper evaluationLogDetailsMapper;
    private final ExperimentResultsDetailsMapper experimentResultsDetailsMapper;
    private final ExperimentResultsMapper experimentResultsMapper;
    private final GetEvaluationResultsMapper evaluationResultsMapper;
    private final EvaluationResultsRequestEntityRepository evaluationResultsRequestEntityRepository;
    private final ExperimentResultsEntityRepository experimentResultsEntityRepository;
    private final ExperimentResultsRequestRepository experimentResultsRequestRepository;

    /**
     * Constructor with spring dependency injection.
     *
     * @param ersRequestService                        - ers request service bean
     * @param evaluationLogDetailsMapper               - evaluation log details mapper bean
     * @param experimentResultsDetailsMapper           - experiment results details mapper bean
     * @param experimentResultsMapper                  - experiment results mapper bean
     * @param evaluationResultsMapper                  - evaluation results mapper bean
     * @param evaluationResultsRequestEntityRepository - evaluation results request repository bean
     * @param experimentResultsEntityRepository        - experiment results entity repository bean
     * @param experimentResultsRequestRepository       - experiment results request entity repository bean
     */
    @Inject
    public ErsService(ErsRequestService ersRequestService,
                      EvaluationLogDetailsMapper evaluationLogDetailsMapper,
                      ExperimentResultsDetailsMapper experimentResultsDetailsMapper,
                      ExperimentResultsMapper experimentResultsMapper,
                      GetEvaluationResultsMapper evaluationResultsMapper,
                      EvaluationResultsRequestEntityRepository evaluationResultsRequestEntityRepository,
                      ExperimentResultsEntityRepository experimentResultsEntityRepository,
                      ExperimentResultsRequestRepository experimentResultsRequestRepository) {
        this.ersRequestService = ersRequestService;
        this.evaluationLogDetailsMapper = evaluationLogDetailsMapper;
        this.experimentResultsDetailsMapper = experimentResultsDetailsMapper;
        this.experimentResultsMapper = experimentResultsMapper;
        this.evaluationResultsMapper = evaluationResultsMapper;
        this.evaluationResultsRequestEntityRepository = evaluationResultsRequestEntityRepository;
        this.experimentResultsEntityRepository = experimentResultsEntityRepository;
        this.experimentResultsRequestRepository = experimentResultsRequestRepository;
    }

    /**
     * Gets ERS report for specified experiment.
     *
     * @param experiment - experiment entity
     * @return ERS report dto
     */
    public ExperimentErsReportDto getErsReport(Experiment experiment) {
        ExperimentErsReportDto experimentErsReportDto = new ExperimentErsReportDto();
        experimentErsReportDto.setExperimentUuid(experiment.getUuid());
        //Gets experiment results list
        List<ExperimentResultsEntity> experimentResultsEntityList =
                experimentResultsEntityRepository.findByExperimentOrderByResultsIndex(experiment);
        experimentErsReportDto.setExperimentResults(experimentResultsMapper.map(experimentResultsEntityList));
        if (!CollectionUtils.isEmpty(experimentResultsEntityList)) {
            experimentErsReportDto.setClassifiersCount(experimentResultsEntityList.size());
            List<Long> experimentResultsIds =
                    experimentResultsEntityList.stream().map(ExperimentResultsEntity::getId).collect(
                            Collectors.toList());
            List<Long> sentResultsIds =
                    experimentResultsEntityRepository.findSentResultsIds(experimentResultsIds);
            experimentErsReportDto.setSentClassifiersCount(sentResultsIds.size());
            //Set sent flag for each experiment results
            experimentErsReportDto.getExperimentResults().forEach(experimentResultsDto -> experimentResultsDto.setSent(
                    sentResultsIds.contains(experimentResultsDto.getId())));
        }
        populateErsReportStatus(experiment, experimentErsReportDto);
        return experimentErsReportDto;
    }

    /**
     * Gets evaluation log report.
     *
     * @param evaluationLog - evaluation log entity
     * @return evaluation log details dto
     */
    public EvaluationLogDetailsDto getEvaluationLogDetails(EvaluationLog evaluationLog) {
        EvaluationLogDetailsDto evaluationLogDetailsDto = evaluationLogDetailsMapper.map(evaluationLog);
        evaluationLogDetailsDto.setEvaluationResultsDto(getEvaluationResults(evaluationLog));
        return evaluationLogDetailsDto;
    }

    /**
     * Gets experiment results details report.
     *
     * @param experimentResultsEntity - experiment results entity
     * @return experiment results details dto
     */
    public ExperimentResultsDetailsDto getExperimentResultsDetails(ExperimentResultsEntity experimentResultsEntity) {
        ExperimentResultsDetailsDto experimentResultsDetailsDto =
                experimentResultsDetailsMapper.map(experimentResultsEntity);
        experimentResultsDetailsDto.setEvaluationResultsDto(getEvaluationResults(experimentResultsEntity));
        return experimentResultsDetailsDto;
    }

    /**
     * Sent experiment results to ERS service.
     *
     * @param experimentResultsEntity - experiment entity
     * @param experimentHistory       - experiment history
     * @param source                  - experiment results request source
     */
    public void sentExperimentResults(ExperimentResultsEntity experimentResultsEntity,
                                      ExperimentHistory experimentHistory, ExperimentResultsRequestSource source) {
        ExperimentResultsRequest experimentResultsRequest = new ExperimentResultsRequest();
        experimentResultsRequest.setRequestSource(source);
        experimentResultsRequest.setExperimentResults(experimentResultsEntity);
        EvaluationResults evaluationResults =
                experimentHistory.getExperiment().get(experimentResultsEntity.getResultsIndex());
        ersRequestService.saveEvaluationResults(evaluationResults, experimentResultsRequest);
    }

    private void populateErsReportStatus(Experiment experiment, ExperimentErsReportDto experimentErsReportDto) {
        ErsReportStatus ersReportStatus;
        if (!RequestStatus.FINISHED.equals(experiment.getExperimentStatus())) {
            ersReportStatus = RequestStatus.NEW.equals(experiment.getExperimentStatus()) ?
                    ErsReportStatus.EXPERIMENT_IN_PROGRESS : ErsReportStatus.EXPERIMENT_ERROR;
            //else handle ERS report status for experiment with FINISHED status
        } else if (experimentErsReportDto.getSentClassifiersCount() == experimentErsReportDto.getClassifiersCount()) {
            ersReportStatus = ErsReportStatus.SUCCESS_SENT;
        } else if (experiment.getDeletedDate() != null) {
            ersReportStatus = ErsReportStatus.EXPERIMENT_DELETED;
        } else {
            ersReportStatus = ErsReportStatus.NEED_SENT;
        }
        experimentErsReportDto.setErsReportStatus(
                new EnumDto(ersReportStatus.name(), ersReportStatus.getDescription()));
    }

    private EvaluationResultsDto getEvaluationResultsFromErs(String requestId) {
        EvaluationResultsStatus evaluationResultsStatus;
        try {
            GetEvaluationResultsResponse evaluationResultsResponse = ersRequestService.getEvaluationResults(requestId);
            return evaluationResultsMapper.map(evaluationResultsResponse);
        } catch (WebServiceIOException ex) {
            log.error(ex.getMessage());
            evaluationResultsStatus = EvaluationResultsStatus.ERS_SERVICE_UNAVAILABLE;
        } catch (Exception ex) {
            log.error("There was an error while fetching evaluation results for request id [{}]: {}", requestId,
                    ex.getMessage());
            evaluationResultsStatus = EvaluationResultsStatus.ERROR;
        }
        return buildEvaluationResultsDto(evaluationResultsStatus);
    }

    private EvaluationResultsDto getEvaluationResults(
            ExperimentResultsEntity experimentResultsEntity) {
        ExperimentResultsRequest experimentResultsRequest = getSuccessExperimentResultsRequest(experimentResultsEntity);
        return experimentResultsRequest == null ? buildEvaluationResultsDto(EvaluationResultsStatus.RESULTS_NOT_SENT) :
                getEvaluationResultsFromErs(experimentResultsRequest.getRequestId());
    }

    private EvaluationResultsDto getEvaluationResults(EvaluationLog evaluationLog) {
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
                return getEvaluationResultsFromErs(evaluationResultsRequestEntity.getRequestId());
            }
        }
        return buildEvaluationResultsDto(evaluationResultsStatus);
    }

    private ExperimentResultsRequest getSuccessExperimentResultsRequest(ExperimentResultsEntity
                                                                                experimentResultsEntity) {
        return experimentResultsRequestRepository.findSuccessRequests(experimentResultsEntity,
                PageRequest.of(0, 1)).stream().findFirst().orElse(null);
    }
}
