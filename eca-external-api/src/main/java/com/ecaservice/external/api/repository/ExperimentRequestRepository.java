package com.ecaservice.external.api.repository;

import com.ecaservice.external.api.entity.ExperimentRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository to manage with {@link ExperimentRequestEntity} persistence entity.
 *
 * @author Roman Batygin
 */
public interface ExperimentRequestRepository extends JpaRepository<ExperimentRequestEntity, Long> {
}
