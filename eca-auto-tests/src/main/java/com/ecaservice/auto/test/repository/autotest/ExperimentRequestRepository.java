package com.ecaservice.auto.test.repository.autotest;

import com.ecaservice.auto.test.entity.autotest.AutoTestsJobEntity;
import com.ecaservice.auto.test.entity.autotest.ExperimentRequestEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

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
    Optional<ExperimentRequestEntity> findByCorrelationId(String correlationId);

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
     * Finds finished requests ids.
     *
     * @return requests ids list
     */
    @Query("select er.id from ExperimentRequestEntity er where er.stageType = 'REQUEST_FINISHED' order by er.started")
    List<Long> findFinishedRequests();

    /**
     * Finds experiment requests page with specified ids.
     *
     * @param ids - ids list
     * @return experiment requests page
     */
    List<ExperimentRequestEntity> findByIdInOrderByCreated(Collection<Long> ids);
}
