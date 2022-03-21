package com.ecaservice.auto.test.repository.autotest;

import com.ecaservice.auto.test.entity.autotest.AutoTestsJobEntity;
import com.ecaservice.auto.test.entity.autotest.BaseEvaluationRequestEntity;
import com.ecaservice.auto.test.entity.autotest.RequestStageType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Repository to manage with {@link BaseEvaluationRequestEntity} persistence entity.
 *
 * @author Roman Batygin
 */
public interface BaseEvaluationRequestRepository extends JpaRepository<BaseEvaluationRequestEntity, Long> {

    /**
     * Finds exceeded requests ids.
     *
     * @param dateTime - date time value
     * @return requests ids list
     */
    @Query("select er.id from BaseEvaluationRequestEntity er where er.stageType not in (:finishedStages)" +
            "and er.started < :dateTime order by er.started")
    List<Long> findExceededRequestIds(@Param("dateTime") LocalDateTime dateTime,
                                      @Param("finishedStages") Collection<RequestStageType> finishedStages);

    /**
     * Finds requests page with specified ids.
     *
     * @param ids - ids list
     * @return requests page
     */
    List<BaseEvaluationRequestEntity> findByIdInOrderByCreated(Collection<Long> ids);

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
}