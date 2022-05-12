package com.ecaservice.server.service.experiment;

import com.ecaservice.classifier.options.adapter.ClassifierOptionsAdapter;
import com.ecaservice.server.mapping.ExperimentResultsMapper;
import com.ecaservice.server.model.entity.ErsResponseStatus;
import com.ecaservice.server.model.entity.Experiment;
import com.ecaservice.server.model.entity.ExperimentResultsEntity;
import com.ecaservice.server.model.entity.ExperimentResultsRequest;
import com.ecaservice.server.model.entity.RequestStatus;
import com.ecaservice.server.repository.ExperimentResultsEntityRepository;
import com.ecaservice.server.repository.ExperimentResultsRequestRepository;
import com.ecaservice.server.service.ers.ErsService;
import com.ecaservice.web.dto.model.EnumDto;
import com.ecaservice.web.dto.model.ErsReportStatus;
import com.ecaservice.web.dto.model.EvaluationResultsDto;
import com.ecaservice.web.dto.model.EvaluationResultsStatus;
import com.ecaservice.web.dto.model.ExperimentErsReportDto;
import com.ecaservice.web.dto.model.ExperimentResultsDetailsDto;
import eca.core.evaluation.EvaluationResults;
import eca.dataminer.AbstractExperiment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import weka.classifiers.AbstractClassifier;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.ecaservice.server.util.ClassifierOptionsHelper.toJsonString;
import static com.ecaservice.server.util.Utils.buildEvaluationResultsDto;

/**
 * Experiment results service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ExperimentResultsService {

    private final ErsService ersService;
    private final ExperimentResultsMapper experimentResultsMapper;
    private final ClassifierOptionsAdapter classifierOptionsAdapter;
    private final ExperimentResultsEntityRepository experimentResultsEntityRepository;
    private final ExperimentResultsRequestRepository experimentResultsRequestRepository;

    /**
     * Saves experiments results that should be sent to ERS.
     *
     * @param experiment        - experiment entity
     * @param experimentHistory - experiment history
     * @return experiment results entities list
     */
    public List<ExperimentResultsEntity> saveExperimentResultsToErsSent(Experiment experiment,
                                                                        AbstractExperiment<?> experimentHistory) {
        log.info("Starting to save experiment [{}] results to ERS sent", experiment.getRequestId());
        List<EvaluationResults> evaluationResultsList = experimentHistory.getHistory();
        List<ExperimentResultsEntity> experimentResultsEntities =
                IntStream.range(0, evaluationResultsList.size()).mapToObj(i -> {
                    EvaluationResults evaluationResults = evaluationResultsList.get(i);
                    ExperimentResultsEntity experimentResultsEntity =
                            experimentResultsMapper.map(evaluationResults);
                    var classifierOptions =
                            classifierOptionsAdapter.convert((AbstractClassifier) evaluationResults.getClassifier());
                    experimentResultsEntity.getClassifierInfo().setClassifierOptions(toJsonString(classifierOptions));
                    experimentResultsEntity.setExperiment(experiment);
                    experimentResultsEntity.setResultsIndex(i);
                    return experimentResultsEntity;
                }).collect(Collectors.toList());
        List<ExperimentResultsEntity> saved = experimentResultsEntityRepository.saveAll(experimentResultsEntities);
        log.info("[{}] experiment [{}] results has been saved to ERS sent", saved.size(), experiment.getRequestId());
        return saved;
    }

    /**
     * Gets experiment results details report.
     *
     * @param experimentResultsEntity - experiment results entity
     * @return experiment results details dto
     */
    public ExperimentResultsDetailsDto getExperimentResultsDetails(ExperimentResultsEntity experimentResultsEntity) {
        ExperimentResultsDetailsDto experimentResultsDetailsDto =
                experimentResultsMapper.mapDetails(experimentResultsEntity);
        experimentResultsDetailsDto.setEvaluationResultsDto(getEvaluationResults(experimentResultsEntity));
        return experimentResultsDetailsDto;
    }

    /**
     * Gets ERS report for specified experiment.
     *
     * @param experiment - experiment entity
     * @return ERS report dto
     */
    public ExperimentErsReportDto getErsReport(Experiment experiment) {
        ExperimentErsReportDto experimentErsReportDto = new ExperimentErsReportDto();
        experimentErsReportDto.setExperimentRequestId(experiment.getRequestId());
        //Gets experiment results list
        List<ExperimentResultsEntity> experimentResultsEntityList =
                experimentResultsEntityRepository.findByExperimentOrderByResultsIndex(experiment);
        experimentErsReportDto.setExperimentResults(experimentResultsEntityList
                .stream()
                .map(experimentResultsMapper::map)
                .collect(Collectors.toList()));
        if (!CollectionUtils.isEmpty(experimentResultsEntityList)) {
            experimentErsReportDto.setClassifiersCount(experimentResultsEntityList.size());
            List<Long> experimentResultsIds = experimentResultsEntityList
                    .stream()
                    .map(ExperimentResultsEntity::getId)
                    .collect(Collectors.toList());
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

    private void populateErsReportStatus(Experiment experiment, ExperimentErsReportDto experimentErsReportDto) {
        ErsReportStatus ersReportStatus;
        if (!RequestStatus.FINISHED.equals(experiment.getRequestStatus())) {
            ersReportStatus = handleNotFinishedExperiment(experiment);
            //else handle ERS report status for experiment with FINISHED status
        } else if (experimentErsReportDto.getClassifiersCount() == 0L) {
            ersReportStatus = ErsReportStatus.EXPERIMENT_RESULTS_NOT_FOUND;
        } else if (experimentErsReportDto.getSentClassifiersCount() == experimentErsReportDto.getClassifiersCount()) {
            ersReportStatus = ErsReportStatus.SUCCESS_SENT;
        } else if (experiment.getDeletedDate() != null) {
            ersReportStatus = ErsReportStatus.EXPERIMENT_DELETED;
        } else {
            ersReportStatus = ErsReportStatus.NOT_SENT;
        }
        experimentErsReportDto.setErsReportStatus(
                new EnumDto(ersReportStatus.name(), ersReportStatus.getDescription()));
    }

    private ErsReportStatus handleNotFinishedExperiment(Experiment experiment) {
        if (RequestStatus.NEW.equals(experiment.getRequestStatus())) {
            return ErsReportStatus.EXPERIMENT_NEW;
        } else if (RequestStatus.IN_PROGRESS.equals(experiment.getRequestStatus())) {
            return ErsReportStatus.EXPERIMENT_IN_PROGRESS;
        } else {
            return ErsReportStatus.EXPERIMENT_ERROR;
        }
    }

    private EvaluationResultsDto getEvaluationResults(ExperimentResultsEntity experimentResultsEntity) {
        ExperimentResultsRequest experimentResultsRequest =
                experimentResultsRequestRepository.findByExperimentResults(experimentResultsEntity);
        if (experimentResultsRequest != null &&
                ErsResponseStatus.SUCCESS.equals(experimentResultsRequest.getResponseStatus())) {
            return ersService.getEvaluationResultsFromErs(experimentResultsRequest.getRequestId());
        } else {
            return buildEvaluationResultsDto(EvaluationResultsStatus.RESULTS_NOT_SENT);
        }
    }
}
