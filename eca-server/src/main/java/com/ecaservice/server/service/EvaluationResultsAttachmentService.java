package com.ecaservice.server.service;

import com.ecaservice.common.web.exception.InvalidFileException;
import com.ecaservice.s3.client.minio.model.UploadObject;
import com.ecaservice.s3.client.minio.service.MinioStorageService;
import com.ecaservice.server.model.EvaluationAttachmentData;
import com.ecaservice.server.model.entity.EvaluationResultsAttachmentEntity;
import com.ecaservice.server.model.entity.EvaluationResultsAttachmentType;
import com.ecaservice.server.repository.EvaluationResultsAttachmentRepository;
import lombok.Cleanup;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;

import static com.ecaservice.common.web.util.FileUtils.isValidExtension;
import static com.ecaservice.common.web.util.FileUtils.openInputStream;

/**
 * Evaluation results attachment service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EvaluationResultsAttachmentService {

    private static final List<String> ATTACHMENT_EXTENSIONS = List.of("png");

    private final EvaluationResultsAttachmentRepository evaluationResultsAttachmentRepository;
    private final MinioStorageService minioStorageService;

    /**
     * Saves evaluation results attachment.
     *
     * @param file                     - attachment file
     * @param evaluationAttachmentData - attachment data
     */
    public void saveAttachment(MultipartFile file, EvaluationAttachmentData evaluationAttachmentData) {
        log.info("Starting to save evaluation results attachment file [{}], data [{}]", file.getOriginalFilename(),
                evaluationAttachmentData);
        if (!isValidExtension(file.getOriginalFilename(), ATTACHMENT_EXTENSIONS)) {
            throw new InvalidFileException(
                    String.format("Invalid file [%s] extension. Expected one of %s", file.getOriginalFilename(),
                            ATTACHMENT_EXTENSIONS));
        }
        minioStorageService.uploadObject(
                UploadObject.builder()
                        .contentType(MediaType.IMAGE_PNG_VALUE)
                        .inputStream(() -> openInputStream(file))
                        .objectPath(evaluationAttachmentData.getFilePath())
                        .build()
        );
        saveOrUpdateAttachmentEntity(evaluationAttachmentData);
        log.info("Evaluation results attachment file [{}], data [{}] has been saved", file.getOriginalFilename(),
                evaluationAttachmentData);
    }

    /**
     * Gets evaluation results attachment bytes.
     *
     * @param key            - attachment key
     * @param attachmentType - attachment type
     * @return evaluation results attachment bytes
     */
    public byte[] getAttachment(String key, EvaluationResultsAttachmentType attachmentType) {
        log.info("Gets evaluation results attachment [{}], type [{}]", key, attachmentType);
        var evaluationResultsAttachmentEntity =
                evaluationResultsAttachmentRepository.findByKeyAndAttachmentType(key, attachmentType);
        if (evaluationResultsAttachmentEntity == null) {
            log.warn("Evaluation results attachment [{}], type [{}] not found", key, attachmentType);
            return null;
        }
        try {
            @Cleanup InputStream inputStream =
                    minioStorageService.downloadObject(evaluationResultsAttachmentEntity.getFilePath());
            byte[] attachmentBytes = IOUtils.toByteArray(inputStream);
            log.info("Evaluation results attachment [{}], type [{}] has been downloaded from file [{}]", key,
                    attachmentType, evaluationResultsAttachmentEntity.getFilePath());
            return attachmentBytes;
        } catch (IOException ex) {
            log.error("Error while download attachment [{}]: {}", evaluationResultsAttachmentEntity.getFilePath(),
                    ex.getMessage(), ex);
            throw new IllegalStateException(ex);
        }
    }

    private void saveOrUpdateAttachmentEntity(EvaluationAttachmentData evaluationAttachmentData) {
        var evaluationResultsAttachmentEntity =
                evaluationResultsAttachmentRepository.findByKeyAndAttachmentType(evaluationAttachmentData.getKey(),
                        evaluationAttachmentData.getAttachmentType());
        if (evaluationResultsAttachmentEntity == null) {
            evaluationResultsAttachmentEntity = new EvaluationResultsAttachmentEntity();
            evaluationResultsAttachmentEntity.setAttachmentType(evaluationAttachmentData.getAttachmentType());
            evaluationResultsAttachmentEntity.setKey(evaluationAttachmentData.getKey());
            evaluationResultsAttachmentEntity.setCreatedDate(LocalDateTime.now());
        }
        evaluationResultsAttachmentEntity.setFilePath(evaluationAttachmentData.getFilePath());
        evaluationResultsAttachmentEntity.setUpdatedDate(LocalDateTime.now());
        evaluationResultsAttachmentRepository.save(evaluationResultsAttachmentEntity);
    }
}
