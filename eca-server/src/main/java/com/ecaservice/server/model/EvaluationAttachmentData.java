package com.ecaservice.server.model;

import com.ecaservice.server.model.entity.EvaluationResultsAttachmentType;
import lombok.Builder;
import lombok.Data;

/**
 * Evaluation attachment data.
 *
 * @author Roman Batygin
 */
@Data
@Builder
public class EvaluationAttachmentData {

    /**
     * Attachment key
     */
    private String key;

    /**
     * Attachment type
     */
    private EvaluationResultsAttachmentType attachmentType;

    /**
     * File path in object storage
     */
    private String filePath;
}
