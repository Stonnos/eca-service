package com.ecaservice.service.experiment;

import com.ecaservice.config.ExperimentConfig;
import com.ecaservice.mapping.ExperimentResultsMapper;
import com.ecaservice.model.entity.Experiment;
import com.ecaservice.model.entity.ExperimentResultsEntity;
import com.ecaservice.repository.ExperimentResultsEntityRepository;
import eca.converters.model.ExperimentHistory;
import eca.core.evaluation.EvaluationResults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Experiment results service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
public class ExperimentResultsService {

    private final ExperimentConfig experimentConfig;
    private final ExperimentResultsMapper experimentResultsMapper;
    private final ExperimentResultsEntityRepository experimentResultsEntityRepository;

    @Inject
    public ExperimentResultsService(ExperimentConfig experimentConfig,
                                    ExperimentResultsMapper experimentResultsMapper,
                                    ExperimentResultsEntityRepository experimentResultsEntityRepository) {
        this.experimentConfig = experimentConfig;
        this.experimentResultsMapper = experimentResultsMapper;
        this.experimentResultsEntityRepository = experimentResultsEntityRepository;
    }

    /**
     * Saves experiments results that should be sent to ERS.
     *
     * @param experiment        - experiment entity
     * @param experimentHistory - experiment history
     * @return experiment results entities list
     */
    public List<ExperimentResultsEntity> saveExperimentResultsToErsSent(Experiment experiment,
                                                                        ExperimentHistory experimentHistory) {
        List<EvaluationResults> evaluationResultsList = experimentHistory.getExperiment();
        int resultsSize = Integer.min(evaluationResultsList.size(), experimentConfig.getResultSizeToSend());
        List<ExperimentResultsEntity> experimentResultsEntities = IntStream.range(0, resultsSize).mapToObj(i -> {
            EvaluationResults evaluationResults = evaluationResultsList.get(i);
            ExperimentResultsEntity experimentResultsEntity =
                    experimentResultsMapper.map(evaluationResults);
            experimentResultsEntity.setExperiment(experiment);
            experimentResultsEntity.setResultsIndex(i);
            return experimentResultsEntity;
        }).collect(Collectors.toList());
        return experimentResultsEntityRepository.saveAll(experimentResultsEntities);
    }
}
