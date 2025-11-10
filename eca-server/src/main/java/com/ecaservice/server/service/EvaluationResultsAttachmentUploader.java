package com.ecaservice.server.service;

import com.ecaservice.server.model.entity.EvaluationResultsAttachmentType;
import org.springframework.web.multipart.MultipartFile;

/**
 * Evaluation results attachment provider interface.
 *
 * @author Roman Batygin
 */
public interface EvaluationResultsAttachmentUploader {

    /**
     * Uploads evaluation results attachment.
     *
     * @param modelId        - evaluation model id
     * @param file           - attachment file
     * @param attachmentType - attachment type
     */
    void uploadAttachment(Long modelId, MultipartFile file, EvaluationResultsAttachmentType attachmentType);
}
