package com.ecaservice.external.api.repository;

import com.ecaservice.external.api.entity.EvaluationRequestEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Repository to manage with {@link EvaluationRequestEntity} persistence entity.
 *
 * @author Roman Batygin
 */
public interface EvaluationRequestRepository extends JpaRepository<EvaluationRequestEntity, Long> {

    /**
     * Finds evaluation request entity by correlation id.
     *
     * @param correlationId - correlation id
     * @return evaluation request entity
     */
    Optional<EvaluationRequestEntity> findByCorrelationId(String correlationId);

    /**
     * Finds evaluation requests ids with not deleted classifiers models.
     *
     * @param dateTime - date time threshold value
     * @return evaluation requests ids
     */
    @Query("select evr.id from EvaluationRequestEntity evr where evr.requestStage = 'COMPLETED' " +
            "and evr.deletedDate is null and evr.endDate < :dateTime order by evr.endDate")
    List<Long> findNotDeletedModels(@Param("dateTime") LocalDateTime dateTime);

    /**
     * Finds evaluation requests page by ids.
     *
     * @param ids      - ids list
     * @param pageable - pageable object
     * @return evaluation requests page
     */
    Page<EvaluationRequestEntity> findByIdIn(Collection<Long> ids, Pageable pageable);
}
