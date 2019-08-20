package com.ecaservice.repository;

import com.ecaservice.model.entity.ExperimentResultsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository to manage with {@link ExperimentResultsEntity} persistence entity.
 *
 * @author Roman Batygin
 */
public interface ExperimentResultsEntityRepository extends JpaRepository<ExperimentResultsEntity, Long> {
}
