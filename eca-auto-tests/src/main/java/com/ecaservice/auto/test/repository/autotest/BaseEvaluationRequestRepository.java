package com.ecaservice.auto.test.repository.autotest;

import com.ecaservice.auto.test.entity.autotest.BaseEvaluationRequestEntity;
import com.ecaservice.auto.test.entity.autotest.RequestStageType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

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
}
