package com.ecaservice.server.service.experiment;

import com.ecaservice.common.web.exception.EntityNotFoundException;
import com.ecaservice.server.model.entity.Experiment;
import com.ecaservice.server.model.entity.ExperimentResultsEntity;
import com.ecaservice.server.repository.ExperimentResultsEntityRepository;
import com.ecaservice.server.service.ModelProvider;
import com.ecaservice.server.service.RocCurveDataProvider;
import com.ecaservice.server.service.RocCurveHelper;
import com.ecaservice.web.dto.model.RocCurveDataDto;
import eca.core.evaluation.EvaluationResults;
import eca.dataminer.AbstractExperiment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import static com.ecaservice.server.util.ValidationUtils.checkClassIndex;
import static com.ecaservice.server.util.ValidationUtils.checkFinishedRequestStatus;
import static com.ecaservice.server.util.ValidationUtils.checkModelNotDeleted;

/**
 * Experiment results roc curve data provider.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ExperimentResultsRocCurveDataProvider implements RocCurveDataProvider {

    private final ModelProvider modelProvider;
    private final ExperimentResultsEntityRepository experimentResultsEntityRepository;

    @Override
    public RocCurveDataDto getRocCurveData(Long modelId, Integer classValueIndex) {
        log.info("Starting to calculate roc curve data for experiment results [{}], class index [{}]", modelId,
                classValueIndex);
        ExperimentResultsEntity experimentResultsEntity = experimentResultsEntityRepository.findById(modelId)
                .orElseThrow(() -> new EntityNotFoundException(ExperimentResultsEntity.class, modelId));
        Experiment experiment = experimentResultsEntity.getExperiment();
        checkClassIndex(classValueIndex, experiment.getInstancesInfo().getNumClasses());
        checkFinishedRequestStatus(experiment);
        checkModelNotDeleted(experiment);
        Assert.notNull(experiment.getModelPath(),
                String.format("Evaluation [%d] model math must be not empty", modelId));
        AbstractExperiment<?> abstractExperiment =
                modelProvider.loadModel(experiment.getModelPath(), AbstractExperiment.class);
        EvaluationResults evaluationResults =
                abstractExperiment.getHistory().get(experimentResultsEntity.getResultsIndex());
        RocCurveDataDto rocCurveDataDto =
                RocCurveHelper.calculateRocCurveData(evaluationResults.getEvaluation(), classValueIndex);
        log.info("Roc curve data has been calculated for experiment results [{}], class index [{}]", modelId,
                classValueIndex);
        return rocCurveDataDto;
    }
}
