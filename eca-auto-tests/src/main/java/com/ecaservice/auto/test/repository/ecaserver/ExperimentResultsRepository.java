package com.ecaservice.auto.test.repository.ecaserver;

import com.ecaservice.auto.test.entity.ecaserver.Experiment;
import com.ecaservice.auto.test.entity.ecaserver.ExperimentResultsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repository to manage with {@link ExperimentResultsEntity} persistence entity.
 *
 * @author Roman Batygin
 */
public interface ExperimentResultsRepository extends JpaRepository<ExperimentResultsEntity, Long> {

    /**
     * Finds experiment results list by specified experiment.
     *
     * @param experiment - experiment entity
     * @return experiment results list
     */
    List<ExperimentResultsEntity> findByExperimentOrderByResultsIndex(Experiment experiment);
}
