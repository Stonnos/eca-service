package com.ecaservice.auto.test.repository.autotest;

import com.ecaservice.auto.test.entity.autotest.AutoTestsJobEntity;
import com.ecaservice.auto.test.entity.autotest.BaseEvaluationRequestEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.Collection;
import java.util.List;

/**
 * Base repository for entities extended {@link BaseEvaluationRequestEntity} entity.
 *
 * @author Roman Batygin
 */
@NoRepositoryBean
public interface JpaBaseEvaluationRequestRepository<E extends BaseEvaluationRequestEntity>
        extends JpaRepository<E, Long> {

    /**
     * Finds request with correlation id.
     *
     * @param correlationId - correlation id
     * @return request entity
     */
    E findByCorrelationId(String correlationId);

    /**
     * Finds request by request id
     *
     * @param requestId - request id
     * @return request entity
     */
    E findByRequestId(String requestId);

    /**
     * Gets requests page for specified job.
     *
     * @param autoTestsJobEntity - auto tests job
     * @param pageable           - pageable object
     * @return requests page
     */
    Page<E> findAllByJob(AutoTestsJobEntity autoTestsJobEntity, Pageable pageable);

    /**
     * Finds requests page with specified ids.
     *
     * @param ids - ids list
     * @return requests page
     */
    List<E> findByIdInOrderByCreated(Collection<Long> ids);
}
