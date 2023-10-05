package com.ecaservice.server.service.evaluation;

import com.ecaservice.classifier.options.adapter.ClassifierOptionsAdapter;
import com.ecaservice.server.config.CrossValidationConfig;
import com.ecaservice.server.mapping.EvaluationLogMapper;
import com.ecaservice.server.model.entity.Channel;
import com.ecaservice.server.model.entity.EvaluationLog;
import com.ecaservice.server.model.entity.RequestStatus;
import com.ecaservice.server.model.evaluation.AbstractEvaluationRequestDataModel;
import com.ecaservice.server.model.evaluation.EvaluationMessageRequestDataModel;
import com.ecaservice.server.model.evaluation.EvaluationRequestDataVisitor;
import com.ecaservice.server.model.evaluation.EvaluationWebRequestDataModel;
import com.ecaservice.server.repository.EvaluationLogRepository;
import com.ecaservice.server.service.InstancesInfoService;
import com.ecaservice.server.service.data.InstancesMetaDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import weka.classifiers.AbstractClassifier;

import java.time.LocalDateTime;
import java.util.List;

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

    private static final List<RequestStatus> FINAL_STATUSES =
            List.of(RequestStatus.FINISHED, RequestStatus.ERROR, RequestStatus.TIMEOUT);

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
    public EvaluationLog createAndSaveEvaluationLog(AbstractEvaluationRequestDataModel evaluationRequestDataModel) {
        var instancesMetaDataModel =
                instancesMetaDataService.getInstancesMetaData(evaluationRequestDataModel.getDataUuid());
        var instancesInfo = instancesInfoService.getOrSaveInstancesInfo(instancesMetaDataModel);
        EvaluationLog evaluationLog = evaluationLogMapper.map(evaluationRequestDataModel, crossValidationConfig);
        evaluationLog.setInstancesInfo(instancesInfo);
        saveClassifierOptions(evaluationRequestDataModel.getClassifier(), evaluationLog);
        setAdditionalProperties(evaluationLog, evaluationRequestDataModel);
        evaluationLog.setRequestStatus(RequestStatus.NEW);
        evaluationLog.setRequestId(evaluationRequestDataModel.getRequestId());
        evaluationLog.setCreationDate(LocalDateTime.now());
        evaluationLogRepository.save(evaluationLog);
        log.info("Evaluation log [{}] has been saved", evaluationLog.getRequestId());
        return evaluationLog;
    }

    /**
     * Starts classifier evaluation.
     *
     * @param evaluationLog - evaluation log
     */
    public void startEvaluationLog(EvaluationLog evaluationLog) {
        evaluationLog.setRequestStatus(RequestStatus.IN_PROGRESS);
        evaluationLog.setStartDate(LocalDateTime.now());
        evaluationLogRepository.save(evaluationLog);
        log.info("Evaluation log [{}] in progress status has been set", evaluationLog.getRequestId());
    }

    /**
     * Finishes classifier evaluation.
     *
     * @param evaluationLog - evaluation log entity
     * @param requestStatus - final request status (FINISHED, ERROR, TIMEOUT)
     */
    public void finishEvaluation(EvaluationLog evaluationLog, RequestStatus requestStatus) {
        if (!FINAL_STATUSES.contains(requestStatus)) {
            throw new IllegalArgumentException(
                    String.format("Invalid final request status [%s] for evaluation log [%s]", requestStatus,
                            evaluationLog.getRequestId()));
        }
        evaluationLog.setRequestStatus(requestStatus);
        evaluationLog.setEndDate(LocalDateTime.now());
        evaluationLogRepository.save(evaluationLog);
        log.info("Evaluation log [{}] has been finished with status [{}]", evaluationLog.getRequestId(), requestStatus);
    }

    private void saveClassifierOptions(AbstractClassifier classifier, EvaluationLog evaluationLog) {
        var classifierOptions = classifierOptionsAdapter.convert(classifier);
        evaluationLog.getClassifierInfo().setClassifierOptions(toJsonString(classifierOptions));
    }

    private void setAdditionalProperties(EvaluationLog evaluationLog,
                                         AbstractEvaluationRequestDataModel evaluationRequestDataModel) {
        evaluationRequestDataModel.visit(new EvaluationRequestDataVisitor() {
            @Override
            public void visit(EvaluationWebRequestDataModel evaluationWebRequestDataModel) {
                evaluationLog.setChannel(Channel.WEB);
                evaluationLog.setCreatedBy(evaluationWebRequestDataModel.getCreatedBy());
            }

            @Override
            public void visit(EvaluationMessageRequestDataModel evaluationMessageRequestDataModel) {
                evaluationLog.setChannel(Channel.QUEUE);
            }
        });
    }
}
