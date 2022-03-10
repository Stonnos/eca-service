package com.ecaservice.auto.test.repository.autotest;

import com.ecaservice.auto.test.entity.autotest.AutoTestsJobEntity;
import com.ecaservice.auto.test.entity.autotest.ExperimentRequestEntity;
import com.ecaservice.auto.test.entity.autotest.RequestStageType;
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
 * Repository to manage with {@link ExperimentRequestEntity} persistence entity.
 *
 * @author Roman Batygin
 */
public interface ExperimentRequestRepository extends JpaRepository<ExperimentRequestEntity, Long> {

    /**
     * Finds experiment request with correlation id and stage.
     *
     * @param correlationId - correlation id
     * @return experiment request entity
     */
    ExperimentRequestEntity findByCorrelationId(String correlationId);

    /**
     * Finds experiment request by request id
     *
     * @param requestId - request id
     * @return experiment request entity
     */
    Optional<ExperimentRequestEntity> findByRequestId(String requestId);

    /**
     * Gets experiment requests page for specified job.
     *
     * @param autoTestsJobEntity - auto tests job
     * @param pageable           - pageable object
     * @return experiment requests page
     */
    Page<ExperimentRequestEntity> findAllByJob(AutoTestsJobEntity autoTestsJobEntity, Pageable pageable);

    /**
     * Finds exceeded requests ids.
     *
     * @param dateTime - date time value
     * @return requests ids list
     */
    @Query("select er.id from ExperimentRequestEntity er where er.stageType not in (:finishedStages)" +
            "and er.started < :dateTime order by er.started")
    List<Long> findExceededRequestIds(@Param("dateTime") LocalDateTime dateTime,
                                      @Param("finishedStages") Collection<RequestStageType> finishedStages);

    /**
     * Finds finished requests ids.
     *
     * @return requests ids list
     */
    @Query("select er.id from ExperimentRequestEntity er where er.stageType = 'REQUEST_FINISHED' order by er.started")
    List<Long> findFinishedRequests();

    /**
     * Gets max experiment request finished date for specified auto test job.
     *
     * @param autoTestsJobEntity - auto test job entity
     * @return max finished date
     */
    @Query("select max(er.finished) from ExperimentRequestEntity er where er.job = :job")
    Optional<LocalDateTime> getMaxFinishedDate(@Param("job") AutoTestsJobEntity autoTestsJobEntity);

    /**
     * Gets min experiment request started date for specified auto test job.
     *
     * @param autoTestsJobEntity - auto tests job
     * @return min started date
     */
    @Query("select min(er.started) from ExperimentRequestEntity er where er.job = :job")
    Optional<LocalDateTime> getMinStartedDate(@Param("job") AutoTestsJobEntity autoTestsJobEntity);

    /**
     * Finds experiment requests page with specified ids.
     *
     * @param ids - ids list
     * @return experiment requests page
     */
    List<ExperimentRequestEntity> findByIdIn(Collection<Long> ids);
}
