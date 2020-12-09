package com.ecaservice.load.test.repository;

import com.ecaservice.load.test.entity.EvaluationRequestEntity;
import com.ecaservice.load.test.entity.LoadTestEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

/**
 * Repository to manage with {@link EvaluationRequestEntity} persistence entity.
 *
 * @author Roman Batygin
 */
public interface EvaluationRequestRepository extends JpaRepository<EvaluationRequestEntity, Long> {

    /**
     * Finds evaluation requests for specified load test entity.
     *
     * @param loadTestEntity - load test entity
     * @param pageable       - pageable object
     * @return evaluation requests page
     */
    Page<EvaluationRequestEntity> findByLoadTestEntityOrderByStarted(LoadTestEntity loadTestEntity, Pageable pageable);

    /**
     * Finds evaluation request with correlation id and stage.
     *
     * @param correlationId    - correlation id
     * @return evaluation request entity
     */
    EvaluationRequestEntity findByCorrelationId(String correlationId);

    /**
     * Finds exceeded requests ids.
     *
     * @param dateTime - date time value
     * @return requests ids list
     */
    @Query("select er.id from EvaluationRequestEntity er where er.stageType = 'REQUEST_SENT' " +
            "and er.started < :dateTime order by er.started")
    List<Long> findExceededRequestIds(@Param("dateTime") LocalDateTime dateTime);

    /**
     * Finds evaluation requests page with specified ids.
     *
     * @param ids      - ids list
     * @param pageable - pageable object
     * @return evaluation requests page
     */
    Page<EvaluationRequestEntity> findByIdIn(Collection<Long> ids, Pageable pageable);

    /**
     * Gets max evaluation request finished date for specified load test.
     *
     * @param loadTestEntity - load test entity
     * @return max finished date
     */
    @Query("select max(er.finished) from EvaluationRequestEntity er where er.loadTestEntity = :loadTestEntity")
    LocalDateTime getMaxFinishedDate(@Param("loadTestEntity") LoadTestEntity loadTestEntity);
}
