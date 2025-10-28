package com.ecaservice.server.repository;

import com.ecaservice.server.model.entity.EvaluationResultsAttachmentEntity;
import com.ecaservice.server.model.entity.EvaluationResultsAttachmentType;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Service to manage with {@link EvaluationResultsAttachmentEntity} persistence entity.
 *
 * @author Roman Batygin
 */
public interface EvaluationResultsAttachmentRepository extends JpaRepository<EvaluationResultsAttachmentEntity, Long> {

    /**
     * Finds evaluation results attachment entity by key and type.
     *
     * @param key            - attachment key
     * @param attachmentType - attachment type
     * @return evaluation results attachment entity
     */
    EvaluationResultsAttachmentEntity findByKeyAndAttachmentType(String key,
                                                                 EvaluationResultsAttachmentType attachmentType);
}
