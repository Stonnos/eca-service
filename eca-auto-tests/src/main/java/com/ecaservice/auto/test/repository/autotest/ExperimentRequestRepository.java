package com.ecaservice.auto.test.repository.autotest;

import com.ecaservice.auto.test.entity.autotest.ExperimentRequestEntity;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Repository to manage with {@link ExperimentRequestEntity} persistence entity.
 *
 * @author Roman Batygin
 */
public interface ExperimentRequestRepository extends JpaBaseEvaluationRequestRepository<ExperimentRequestEntity> {

    /**
     * Finds finished requests ids.
     *
     * @return requests ids list
     */
    @Query("select er.id from ExperimentRequestEntity er where er.stageType = 'REQUEST_FINISHED' order by er.started")
    List<Long> findFinishedRequests();
}
