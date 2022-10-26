package com.ecaservice.server.repository;

import com.ecaservice.server.model.entity.ExperimentStepEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository to manage with {@link ExperimentStepEntity} persistence entity.
 *
 * @author Roman Batygin
 */
public interface ExperimentStepRepository extends JpaRepository<ExperimentStepEntity, Long> {
}