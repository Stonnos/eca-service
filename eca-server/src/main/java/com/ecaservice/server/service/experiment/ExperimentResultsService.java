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
import com.ecaservice.server.service.classifiers.ClassifierOptionsInfoProcessor;
import com.ecaservice.server.service.ers.ErsService;
import com.ecaservice.web.dto.model.EnumDto;
import com.ecaservice.web.dto.model.ErsReportStatus;
import com.ecaservice.web.dto.model.EvaluationResultsDto;
import com.ecaservice.web.dto.model.EvaluationResultsStatus;
import com.ecaservice.web.dto.model.ExperimentErsReportDto;
import com.ecaservice.web.dto.model.ExperimentResultsDetailsDto;
import com.ecaservice.web.dto.model.ExperimentResultsDto;
import eca.core.evaluation.EvaluationResults;
import eca.dataminer.AbstractExperiment;
import io.micrometer.tracing.annotation.NewSpan;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import weka.classifiers.AbstractClassifier;

import java.math.BigDecimal;
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
    private final ClassifierOptionsInfoProcessor classifierOptionsInfoProcessor;
    private final ExperimentResultsMapper experimentResultsMapper;
    private final ClassifierOptionsAdapter classifierOptionsAdapter;
    private final ExperimentResultsEntityRepository experimentResultsEntityRepository;
    private final ExperimentResultsRequestRepository experimentResultsRequestRepository;

    /**
     * Saves experiments results.
     *
     * @param experiment        - experiment entity
     * @param experimentHistory - experiment history
     * @return experiment results entities list
     */
    @NewSpan
    public List<ExperimentResultsEntity> saveExperimentResults(Experiment experiment,
                                                               AbstractExperiment<?> experimentHistory) {
        log.info("Starting to save experiment [{}] results to ERS sent", experiment.getRequestId());
        List<EvaluationResults> evaluationResultsList = experimentHistory.getHistory();
        List<ExperimentResultsEntity> experimentResultsEntities =
                IntStream.range(0, evaluationResultsList.size()).mapToObj(i -> {
                    EvaluationResults evaluationResults = evaluationResultsList.get(i);
                    return createExperimentResultsEntity(evaluationResults, experiment, i);
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
        log.info("Starting to get experiment [{}] result details [{}]",
                experimentResultsEntity.getExperiment().getRequestId(), experimentResultsEntity.getId());
        ExperimentResultsDetailsDto experimentResultsDetailsDto =
                experimentResultsMapper.mapDetails(experimentResultsEntity);
        experimentResultsDetailsDto.setClassifierInfo(
                classifierOptionsInfoProcessor.processClassifierInfo(experimentResultsEntity.getClassifierOptions()));
        experimentResultsDetailsDto.setEvaluationResultsDto(getEvaluationResults(experimentResultsEntity));
        log.info("Experiment [{}] result details [{}] has been fetched",
                experimentResultsEntity.getExperiment().getRequestId(), experimentResultsEntity.getId());
        return experimentResultsDetailsDto;
    }

    /**
     * Gets ERS report for specified experiment.
     *
     * @param experiment - experiment entity
     * @return ERS report dto
     */
    @NewSpan
    public ExperimentErsReportDto getErsReport(Experiment experiment) {
        log.info("Starting to fetch experiment [{}] ERS report", experiment.getRequestId());
        ExperimentErsReportDto experimentErsReportDto = new ExperimentErsReportDto();
        experimentErsReportDto.setExperimentRequestId(experiment.getRequestId());
        if (!RequestStatus.FINISHED.equals(experiment.getRequestStatus())) {
            populateErsReportStatusForNotFinishedExperiment(experiment, experimentErsReportDto);
        } else {
            //Gets experiment results list
            var experimentResultsEntityList =
                    experimentResultsEntityRepository.findByExperimentOrderByResultsIndex(experiment);
            if (CollectionUtils.isEmpty(experimentResultsEntityList)) {
                log.warn("Experiment [{}] results list not found for ERS report", experiment.getRequestId());
                setErsReportStatus(experimentErsReportDto, ErsReportStatus.EXPERIMENT_RESULTS_NOT_FOUND);
            } else {
                var experimentResultsDtoList
                        = mapToExperimentResultsDtoList(experimentResultsEntityList);
                experimentErsReportDto.setExperimentResults(experimentResultsDtoList);
                setErsReportStatus(experimentErsReportDto, ErsReportStatus.FETCHED);
            }
        }
        log.info("Experiment [{}] ERS report has been fetched with status [{}]", experiment.getRequestId(),
                experimentErsReportDto.getErsReportStatus().getValue());
        return experimentErsReportDto;
    }

    private List<ExperimentResultsDto> mapToExperimentResultsDtoList(
            List<ExperimentResultsEntity> experimentResultsEntityList) {
        return experimentResultsEntityList
                .stream()
                .map(experimentResultsEntity -> {
                    var experimentResultsDto = experimentResultsMapper.map(experimentResultsEntity);
                    var classifierInfoDto = classifierOptionsInfoProcessor.processClassifierInfo(
                            experimentResultsEntity.getClassifierOptions());
                    experimentResultsDto.setClassifierInfo(classifierInfoDto);
                    return experimentResultsDto;
                })
                .collect(Collectors.toList());
    }

    private ExperimentResultsEntity createExperimentResultsEntity(EvaluationResults evaluationResults,
                                                                  Experiment experiment,
                                                                  int resultIndex) {
        ExperimentResultsEntity experimentResultsEntity = new ExperimentResultsEntity();
        var classifierOptions =
                classifierOptionsAdapter.convert((AbstractClassifier) evaluationResults.getClassifier());
        experimentResultsEntity.setClassifierName(
                evaluationResults.getClassifier().getClass().getSimpleName());
        experimentResultsEntity.setPctCorrect(
                BigDecimal.valueOf(evaluationResults.getEvaluation().pctCorrect()));
        experimentResultsEntity.setClassifierOptions(toJsonString(classifierOptions));
        experimentResultsEntity.setExperiment(experiment);
        experimentResultsEntity.setResultsIndex(resultIndex);
        return experimentResultsEntity;
    }

    private void populateErsReportStatusForNotFinishedExperiment(Experiment experiment,
                                                                 ExperimentErsReportDto experimentErsReportDto) {
        if (RequestStatus.NEW.equals(experiment.getRequestStatus())) {
            setErsReportStatus(experimentErsReportDto, ErsReportStatus.EXPERIMENT_NEW);
        } else if (RequestStatus.IN_PROGRESS.equals(experiment.getRequestStatus())) {
            setErsReportStatus(experimentErsReportDto, ErsReportStatus.EXPERIMENT_IN_PROGRESS);
        } else {
            setErsReportStatus(experimentErsReportDto, ErsReportStatus.EXPERIMENT_ERROR);
        }
    }

    private void setErsReportStatus(ExperimentErsReportDto experimentErsReportDto, ErsReportStatus ersReportStatus) {
        experimentErsReportDto.setErsReportStatus(
                new EnumDto(ersReportStatus.name(), ersReportStatus.getDescription()));
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
