package com.ecaservice.server.service.evaluation;

import com.ecaservice.common.web.exception.EntityNotFoundException;
import com.ecaservice.server.model.entity.EvaluationLog;
import com.ecaservice.server.repository.EvaluationLogRepository;
import com.ecaservice.server.service.ModelProvider;
import com.ecaservice.server.service.RocCurveDataProvider;
import com.ecaservice.server.service.RocCurveHelper;
import com.ecaservice.web.dto.model.RocCurveDataDto;
import eca.core.model.ClassificationModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import static com.ecaservice.server.util.ValidationUtils.checkClassIndex;
import static com.ecaservice.server.util.ValidationUtils.checkFinishedRequestStatus;
import static com.ecaservice.server.util.ValidationUtils.checkModelNotDeleted;

/**
 * Evaluation roc curve data provider.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EvaluationRocCurveDataProvider implements RocCurveDataProvider {

    private final ModelProvider modelProvider;
    private final EvaluationLogRepository evaluationLogRepository;

    @Override
    public RocCurveDataDto getRocCurveData(Long modelId, Integer classValueIndex) {
        log.info("Starting to calculate roc curve data for evaluation log [{}], class index [{}]", modelId,
                classValueIndex);
        EvaluationLog evaluationLog = evaluationLogRepository.findById(modelId)
                .orElseThrow(() -> new EntityNotFoundException(EvaluationLog.class, modelId));
        checkClassIndex(classValueIndex, evaluationLog.getInstancesInfo().getNumClasses());
        checkFinishedRequestStatus(evaluationLog);
        checkModelNotDeleted(evaluationLog);
        Assert.notNull(evaluationLog.getModelPath(),
                String.format("Evaluation [%d] model math must be not empty", modelId));
        var classificationModel = modelProvider.loadModel(evaluationLog.getModelPath(), ClassificationModel.class);
        RocCurveDataDto rocCurveDataDto =
                RocCurveHelper.calculateRocCurveData(classificationModel.getEvaluation(), classValueIndex);
        log.info("Roc curve data has been calculated for evaluation log [{}], class index [{}]", modelId,
                classValueIndex);
        return rocCurveDataDto;
    }
}
