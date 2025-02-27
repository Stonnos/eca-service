package com.ecaservice.server.service.evaluation;

import com.ecaservice.common.web.exception.EntityNotFoundException;
import com.ecaservice.server.model.entity.EvaluationLog;
import com.ecaservice.server.repository.EvaluationLogRepository;
import com.ecaservice.server.service.ClassifyInstanceHelper;
import com.ecaservice.server.service.ClassifyInstanceService;
import com.ecaservice.server.service.ClassifyInstanceValidator;
import com.ecaservice.server.service.ModelProvider;
import com.ecaservice.web.dto.model.ClassifyInstanceRequestDto;
import com.ecaservice.web.dto.model.ClassifyInstanceResultDto;
import eca.core.model.ClassificationModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import weka.classifiers.AbstractClassifier;

import static com.ecaservice.server.util.ValidationUtils.checkFinishedRequestStatus;
import static com.ecaservice.server.util.ValidationUtils.checkModelNotDeleted;

/**
 * Classify evaluation log instance service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ClassifyEvaluationInstanceService implements ClassifyInstanceService {

    private final ModelProvider modelProvider;
    private final ClassifyInstanceValidator classifyInstanceValidator;
    private final EvaluationLogRepository evaluationLogRepository;

    @Override
    public ClassifyInstanceResultDto classifyInstance(ClassifyInstanceRequestDto classifyInstanceRequestDto) {
        log.info("Starting to classify instance {} for evaluation log [{}]", classifyInstanceRequestDto.getValues(),
                classifyInstanceRequestDto.getModelId());
        var evaluationLog = evaluationLogRepository.findById(classifyInstanceRequestDto.getModelId())
                .orElseThrow(() -> new EntityNotFoundException(EvaluationLog.class,
                        classifyInstanceRequestDto.getModelId()));
        checkFinishedRequestStatus(evaluationLog);
        checkModelNotDeleted(evaluationLog);
        classifyInstanceValidator.validate(classifyInstanceRequestDto, evaluationLog.getInstancesInfo());
        Assert.notNull(evaluationLog.getModelPath(),
                String.format("Evaluation [%d] model math must be not empty", classifyInstanceRequestDto.getModelId()));
        ClassificationModel classificationModel =
                modelProvider.loadModel(evaluationLog.getModelPath(), ClassificationModel.class);
        AbstractClassifier classifier = classificationModel.getClassifier();
        var classifyInstanceResultDto =
                ClassifyInstanceHelper.classifyInstance(classifier, classificationModel.getData(),
                        classifyInstanceRequestDto.getValues());
        log.info("Instances has been classified for evaluation log [{}] with result [{}]",
                classifyInstanceRequestDto.getModelId(), classifyInstanceResultDto);
        return classifyInstanceResultDto;
    }
}
