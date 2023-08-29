package com.ecaservice.server.service.evaluation;

import com.ecaservice.classifier.options.adapter.ClassifierOptionsAdapter;
import com.ecaservice.server.config.CrossValidationConfig;
import com.ecaservice.server.mapping.EvaluationLogMapper;
import com.ecaservice.server.model.entity.EvaluationLog;
import com.ecaservice.server.model.entity.RequestStatus;
import com.ecaservice.server.model.evaluation.ClassificationResult;
import com.ecaservice.server.model.evaluation.EvaluationRequestDataModel;
import com.ecaservice.server.repository.EvaluationLogRepository;
import com.ecaservice.server.service.InstancesInfoService;
import com.ecaservice.server.service.data.InstancesMetaDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import weka.classifiers.AbstractClassifier;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static com.ecaservice.server.util.ClassifierOptionsHelper.toJsonString;

/**
 * Evaluation log service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EvaluationLogService {

    private final CrossValidationConfig crossValidationConfig;
    private final EvaluationLogRepository evaluationLogRepository;
    private final EvaluationLogMapper evaluationLogMapper;
    private final ClassifierOptionsAdapter classifierOptionsAdapter;
    private final InstancesInfoService instancesInfoService;
    private final InstancesMetaDataService instancesMetaDataService;

    /**
     * Creates evaluation log.
     *
     * @param evaluationRequestDataModel - evaluation request data model
     * @return evaluation log entity
     */
    public EvaluationLog createAndSaveEvaluationLog(EvaluationRequestDataModel evaluationRequestDataModel) {
        var instancesMetaDataModel =
                instancesMetaDataService.getInstancesMetaData(evaluationRequestDataModel.getDataUuid());
        var instancesInfo = instancesInfoService.getOrSaveInstancesInfo(instancesMetaDataModel);
        EvaluationLog evaluationLog = evaluationLogMapper.map(evaluationRequestDataModel, crossValidationConfig);
        evaluationLog.setInstancesInfo(instancesInfo);
        processClassifierOptions(evaluationRequestDataModel.getClassifier(), evaluationLog);
        evaluationLog.setRequestStatus(RequestStatus.IN_PROGRESS);
        evaluationLog.setRequestId(evaluationRequestDataModel.getRequestId());
        evaluationLog.setCreationDate(LocalDateTime.now());
        evaluationLog.setStartDate(LocalDateTime.now());
        evaluationLogRepository.save(evaluationLog);
        log.info("Evaluation log [{}] has been saved", evaluationLog.getRequestId());
        return evaluationLog;
    }

    /**
     * Handles success evaluation.
     *
     * @param classificationResult - evaluation results
     * @param evaluationLog        - evaluation log entity
     */
    public void handleSuccess(ClassificationResult classificationResult,
                              EvaluationLog evaluationLog) {
        evaluationLog.setRequestStatus(RequestStatus.FINISHED);
        double pctCorrect = classificationResult.getEvaluationResults().getEvaluation().pctCorrect();
        evaluationLog.setPctCorrect(BigDecimal.valueOf(pctCorrect));
        evaluationLog.setEndDate(LocalDateTime.now());
        evaluationLogRepository.save(evaluationLog);
        log.info("Evaluation log [{}] has been updated with success status", evaluationLog.getRequestId());
    }

    /**
     * Handles error evaluation.
     *
     * @param evaluationLog - evaluation log entity
     * @param errorMessage  - error message
     */
    public void handleError(EvaluationLog evaluationLog, String errorMessage) {
        evaluationLog.setRequestStatus(RequestStatus.ERROR);
        evaluationLog.setErrorMessage(errorMessage);
        evaluationLog.setEndDate(LocalDateTime.now());
        evaluationLogRepository.save(evaluationLog);
        log.info("Evaluation log [{}] has been updated with error status", evaluationLog.getRequestId());
    }

    /**
     * Handles timeout evaluation.
     *
     * @param evaluationLog - evaluation log entity
     */
    public void handleTimeout(EvaluationLog evaluationLog) {
        evaluationLog.setRequestStatus(RequestStatus.TIMEOUT);
        evaluationLog.setEndDate(LocalDateTime.now());
        evaluationLogRepository.save(evaluationLog);
        log.info("Evaluation log [{}] has been updated with timeout status", evaluationLog.getRequestId());
    }

    private void processClassifierOptions(AbstractClassifier classifier, EvaluationLog evaluationLog) {
        var classifierOptions = classifierOptionsAdapter.convert(classifier);
        evaluationLog.getClassifierInfo().setClassifierOptions(toJsonString(classifierOptions));
    }
}
