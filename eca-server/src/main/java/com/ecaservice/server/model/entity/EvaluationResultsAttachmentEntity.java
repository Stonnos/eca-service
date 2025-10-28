package com.ecaservice.server.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Evaluation results attachment persistence entity.
 *
 * @author Roman Batygin
 */
@Data
@Entity
@Table(name = "evaluation_results_attachment",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"attachment_key", "attachment_type"},
                        name = "evaluation_results_attachment_key_attachment_type_unique_idx")
        })
public class EvaluationResultsAttachmentEntity {

    @Id
    @GeneratedValue
    private Long id;

    /**
     * Attachment key
     */
    @Column(name = "attachment_key", nullable = false)
    private String key;

    /**
     * Attachment type
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "attachment_type", nullable = false)
    private EvaluationResultsAttachmentType attachmentType;

    /**
     * Instances object path
     */
    @Column(name = "file_path", nullable = false)
    private String filePath;

    /**
     * Created date
     */
    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;

    /**
     * Updated date
     */
    @Column(name = "updated_date", nullable = false)
    private LocalDateTime updatedDate;

}
