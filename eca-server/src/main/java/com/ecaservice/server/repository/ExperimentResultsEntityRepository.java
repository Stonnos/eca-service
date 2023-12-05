package com.ecaservice.server.repository;

import com.ecaservice.server.model.entity.Experiment;
import com.ecaservice.server.model.entity.ExperimentResultsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repository to manage with {@link ExperimentResultsEntity} persistence entity.
 *
 * @author Roman Batygin
 */
public interface ExperimentResultsEntityRepository extends JpaRepository<ExperimentResultsEntity, Long> {

    /**
     * Finds experiment results list by specified experiment.
     *
     * @param experiment - experiment entity
     * @return experiment results list
     */
    List<ExperimentResultsEntity> findByExperimentOrderByResultsIndex(Experiment experiment);
}
