package com.ecaservice.server.service.evaluation;

import com.ecaservice.common.web.exception.EntityNotFoundException;
import com.ecaservice.server.model.EvaluationAttachmentData;
import com.ecaservice.server.model.entity.EvaluationLog;
import com.ecaservice.server.model.entity.EvaluationResultsAttachmentType;
import com.ecaservice.server.repository.EvaluationLogRepository;
import com.ecaservice.server.service.EvaluationResultsAttachmentService;
import com.ecaservice.server.service.EvaluationResultsAttachmentUploader;
import com.ecaservice.server.service.ModelProvider;
import com.ecaservice.server.service.RocCurveDataProvider;
import com.ecaservice.server.service.RocCurveHelper;
import com.ecaservice.web.dto.model.RocCurveDataDto;
import eca.core.model.ClassificationModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

import static com.ecaservice.server.util.EvaluationResultsAttachmentKeys.EVALUATION_KEY_FORMAT;
import static com.ecaservice.server.util.ValidationUtils.checkClassIndex;
import static com.ecaservice.server.util.ValidationUtils.checkFinishedRequestStatus;
import static com.ecaservice.server.util.ValidationUtils.checkModelNotDeleted;

/**
 * Evaluation results attachment uploader.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SimpleEvaluationResultsAttachmentUploader implements EvaluationResultsAttachmentUploader {

    private static final String FILE_PATH_FORMAT = "evaluation-results-attachments/classifiers/%s/%s";

    private static final Map<EvaluationResultsAttachmentType, String> FILE_NAMES = Map.of(
            EvaluationResultsAttachmentType.ROC_CURVE_IMAGE, "roc-curve-image.png"
    );

    private final EvaluationResultsAttachmentService evaluationResultsAttachmentService;
    private final EvaluationLogRepository evaluationLogRepository;

    @Override
    public void uploadAttachment(Long modelId, MultipartFile file, EvaluationResultsAttachmentType attachmentType) {
        log.info("Starting to upload evaluation results attachment [{}], type [{}] for evaluation log [{}]",
                file.getOriginalFilename(), attachmentType, modelId);
        EvaluationLog evaluationLog = evaluationLogRepository.findById(modelId)
                .orElseThrow(() -> new EntityNotFoundException(EvaluationLog.class, modelId));
        var evaluationAttachmentData = EvaluationAttachmentData.builder()
                .key(String.format(EVALUATION_KEY_FORMAT, evaluationLog.getRequestId()))
                .attachmentType(attachmentType)
                .filePath(String.format(FILE_PATH_FORMAT, evaluationLog.getRequestId(), FILE_NAMES.get(attachmentType)))
                .build();
        evaluationResultsAttachmentService.saveAttachment(file, evaluationAttachmentData);
        log.info("Evaluation results attachment [{}], type [{}]  has been uploaded for evaluation log [{}]",
                file.getOriginalFilename(), attachmentType, modelId);
    }
}
