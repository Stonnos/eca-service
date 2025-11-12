package com.ecaservice.server.service.evaluation;

import com.ecaservice.common.web.exception.EntityNotFoundException;
import com.ecaservice.s3.client.minio.service.ObjectStorageService;
import com.ecaservice.server.model.data.AttributeMetaInfo;
import com.ecaservice.server.model.entity.EvaluationLog;
import com.ecaservice.server.model.entity.InstancesInfo;
import com.ecaservice.server.model.evaluation.AucPredictionsData;
import com.ecaservice.server.repository.AttributesInfoRepository;
import com.ecaservice.server.repository.EvaluationLogRepository;
import com.ecaservice.server.service.RocCurveDataProvider;
import com.ecaservice.server.service.RocCurveHelper;
import com.ecaservice.web.dto.model.RocCurveDataDto;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import static com.ecaservice.server.util.AttributeUtils.getClassAttribute;
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

    private final ObjectStorageService objectStorageService;
    private final EvaluationLogRepository evaluationLogRepository;
    private final AttributesInfoRepository attributesInfoRepository;

    @Override
    @SneakyThrows
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
        InstancesInfo instancesInfo = evaluationLog.getInstancesInfo();
        var attributesInfo = attributesInfoRepository.findByInstancesInfo(instancesInfo)
                .orElseThrow(() -> new EntityNotFoundException(InstancesInfo.class, instancesInfo.getId()));
        var aucPredictionsData =
                objectStorageService.getObject(evaluationLog.getAucPredictionsPath(), AucPredictionsData.class);
        AttributeMetaInfo classAttribute =
                getClassAttribute(attributesInfo.getAttributes(), instancesInfo.getClassName());
        RocCurveDataDto rocCurveDataDto =
                RocCurveHelper.calculateRocCurveData(aucPredictionsData, classAttribute, classValueIndex);
        log.info("Roc curve data has been calculated for evaluation log [{}], class index [{}]", modelId,
                classValueIndex);
        return rocCurveDataDto;
    }
}
