package com.ecaservice.load.test.repository;

import com.ecaservice.load.test.entity.EvaluationRequestEntity;
import com.ecaservice.load.test.entity.LoadTestEntity;
import com.ecaservice.load.test.projection.TestResultStatistics;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository to manage with {@link EvaluationRequestEntity} persistence entity.
 *
 * @author Roman Batygin
 */
public interface EvaluationRequestRepository extends JpaRepository<EvaluationRequestEntity, Long> {

    /**
     * Finds evaluation request with correlation id and stage.
     *
     * @param correlationId - correlation id
     * @return evaluation request entity
     */
    EvaluationRequestEntity findByCorrelationId(String correlationId);

    /**
     * Gets test results statistics for specified load test.
     *
     * @param loadTestEntity - load test entity
     * @return test results statistics
     */
    @Query("select er.testResult as testResult, count(er) as testResultCount from EvaluationRequestEntity er " +
            "where er.loadTestEntity = :loadTest group by er.testResult")
    List<TestResultStatistics> getTestResultStatistics(@Param("loadTest") LoadTestEntity loadTestEntity);

    /**
     * Finds exceeded requests ids.
     *
     * @param dateTime - date time value
     * @param pageable - pageable
     * @return requests ids list
     */
    @Query("select er from EvaluationRequestEntity er where er.stageType = 'REQUEST_SENT' " +
            "and er.started < :dateTime order by er.started")
    Page<EvaluationRequestEntity> findExceededRequestIds(@Param("dateTime") LocalDateTime dateTime, Pageable pageable);

    /**
     * Gets max evaluation request finished date for specified load test.
     *
     * @param loadTestEntity - load test entity
     * @return max finished date
     */
    @Query("select max(er.finished) from EvaluationRequestEntity er where er.loadTestEntity = :loadTestEntity")
    Optional<LocalDateTime> getMaxFinishedDate(@Param("loadTestEntity") LoadTestEntity loadTestEntity);
}
