package com.ecaservice.server.service.experiment;

import com.ecaservice.common.web.exception.EntityNotFoundException;
import com.ecaservice.server.model.entity.Experiment;
import com.ecaservice.server.model.entity.ExperimentResultsEntity;
import com.ecaservice.server.repository.ExperimentResultsEntityRepository;
import com.ecaservice.server.service.ClassifyInstanceHelper;
import com.ecaservice.server.service.ClassifyInstanceService;
import com.ecaservice.server.service.ClassifyInstanceValidator;
import com.ecaservice.server.service.ModelProvider;
import com.ecaservice.web.dto.model.ClassifyInstanceRequestDto;
import com.ecaservice.web.dto.model.ClassifyInstanceResultDto;
import eca.core.evaluation.EvaluationResults;
import eca.dataminer.AbstractExperiment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import static com.ecaservice.server.util.ValidationUtils.checkFinishedRequestStatus;
import static com.ecaservice.server.util.ValidationUtils.checkModelNotDeleted;

/**
 * Classify experiment results instance service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ClassifyExperimentResultsInstanceService implements ClassifyInstanceService {

    private final ModelProvider modelProvider;
    private final ClassifyInstanceValidator classifyInstanceValidator;
    private final ExperimentResultsEntityRepository experimentResultsEntityRepository;

    @Override
    public ClassifyInstanceResultDto classifyInstance(ClassifyInstanceRequestDto classifyInstanceRequestDto) {
        log.info("Starting to classify instance {} for experiment results [{}]", classifyInstanceRequestDto.getValues(),
                classifyInstanceRequestDto.getModelId());
        Long experimentResultsId = classifyInstanceRequestDto.getModelId();
        var experimentResultsEntity = experimentResultsEntityRepository.findById(experimentResultsId)
                .orElseThrow(() -> new EntityNotFoundException(ExperimentResultsEntity.class, experimentResultsId));
        Experiment experiment = experimentResultsEntity.getExperiment();
        checkFinishedRequestStatus(experiment);
        checkModelNotDeleted(experiment);
        classifyInstanceValidator.validate(classifyInstanceRequestDto, experiment.getInstancesInfo());
        Assert.notNull(experiment.getModelPath(),
                String.format("Experiment [%s] model math must be not empty", experiment.getRequestId()));
        AbstractExperiment<?> abstractExperiment =
                modelProvider.loadModel(experiment.getModelPath(), AbstractExperiment.class);
        EvaluationResults evaluationResults =
                abstractExperiment.getHistory().get(experimentResultsEntity.getResultsIndex());
        var classifyInstanceResultDto = ClassifyInstanceHelper.classifyInstance(evaluationResults.getClassifier(),
                evaluationResults.getEvaluation().getData(), classifyInstanceRequestDto.getValues());
        log.info("Instances has been classified for experiment results [{}] with result [{}]",
                classifyInstanceRequestDto.getModelId(), classifyInstanceResultDto);
        return classifyInstanceResultDto;
    }
}
