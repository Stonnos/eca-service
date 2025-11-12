package com.ecaservice.server.service.experiment;

import com.ecaservice.common.web.exception.EntityNotFoundException;
import com.ecaservice.server.model.EvaluationAttachmentData;
import com.ecaservice.server.model.entity.EvaluationResultsAttachmentType;
import com.ecaservice.server.model.entity.Experiment;
import com.ecaservice.server.model.entity.ExperimentResultsEntity;
import com.ecaservice.server.repository.ExperimentResultsEntityRepository;
import com.ecaservice.server.service.EvaluationResultsAttachmentService;
import com.ecaservice.server.service.EvaluationResultsAttachmentUploader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

import static com.ecaservice.server.util.EvaluationResultsAttachmentKeys.EXPERIMENT_RESULTS_KEY_FORMAT;
import static com.ecaservice.server.util.S3ObjectPaths.EXPERIMENT_RESULTS_ATTACHMENT_PATH_FORMAT;

/**
 * Experiment results attachment uploader.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ExperimentResultsAttachmentUploader implements EvaluationResultsAttachmentUploader {

    private static final Map<EvaluationResultsAttachmentType, String> FILE_NAMES = Map.of(
            EvaluationResultsAttachmentType.ROC_CURVE_IMAGE, "roc-curve-image-%d.png"
    );

    private final EvaluationResultsAttachmentService evaluationResultsAttachmentService;
    private final ExperimentResultsEntityRepository experimentResultsEntityRepository;

    @Override
    public void uploadAttachment(Long modelId, MultipartFile file, EvaluationResultsAttachmentType attachmentType) {
        log.info("Starting to upload experiment results [{}] attachment [{}], type [{}]",
                modelId, file.getOriginalFilename(), attachmentType);
        ExperimentResultsEntity experimentResultsEntity = experimentResultsEntityRepository.findById(modelId)
                .orElseThrow(() -> new EntityNotFoundException(ExperimentResultsEntity.class, modelId));
        Experiment experiment = experimentResultsEntity.getExperiment();
        String fileName = String.format(FILE_NAMES.get(attachmentType), experimentResultsEntity.getId());
        String filePath = String.format(EXPERIMENT_RESULTS_ATTACHMENT_PATH_FORMAT, experiment.getRequestId(), fileName);
        var evaluationAttachmentData = EvaluationAttachmentData.builder()
                .key(String.format(EXPERIMENT_RESULTS_KEY_FORMAT, experiment.getRequestId(),
                        experimentResultsEntity.getId()))
                .attachmentType(attachmentType)
                .filePath(filePath)
                .build();
        evaluationResultsAttachmentService.saveAttachment(file, evaluationAttachmentData);
        log.info("Experiment results [{}] attachment [{}], type [{}] has been uploaded",
                modelId, file.getOriginalFilename(), attachmentType);
    }
}
