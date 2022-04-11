package com.ecaservice.auto.test.repository.ecaserver;

import com.ecaservice.auto.test.entity.ecaserver.AbstractEvaluationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.Optional;

/**
 * Base repository for entities extended {@link AbstractEvaluationEntity} entity.
 *
 * @param <E> - entity type
 * @author Roman Batygin
 */
@NoRepositoryBean
public interface BaseEvaluationRepository<E extends AbstractEvaluationEntity> extends JpaRepository<E, Long> {

    /**
     * Gets evaluation entity by request id.
     *
     * @param requestId - request id
     * @return evaluation entity
     */
    Optional<E> findByRequestId(String requestId);
}
