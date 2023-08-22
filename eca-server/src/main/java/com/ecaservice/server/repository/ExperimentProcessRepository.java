package com.ecaservice.server.repository;

import com.ecaservice.server.model.entity.Experiment;
import com.ecaservice.server.model.entity.ExperimentProcessEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

/**
 * Repository to manage with {@link ExperimentProcessEntity} persistence entity.
 *
 * @author Roman Batygin
 */
public interface ExperimentProcessRepository extends JpaRepository<ExperimentProcessEntity, Long> {

    /**
     * Gets experiment process entity by process uuid.
     *
     * @param processUuid - process uuid
     * @return experiment process entity
     */
    Optional<ExperimentProcessEntity> findByProcessUuid(String processUuid);

    /**
     * Gets active processes count for specified experiment.
     *
     * @param experiment - experiment entity
     * @return active processed count
     */
    @Query("select count(pr) from ExperimentProcessEntity pr where pr.experiment = :experiment and " +
            "(pr.processStatus = 'READY' or pr.processStatus = 'IN_PROGRESS')")
    long getActiveProcessesCount(@Param("experiment") Experiment experiment);
}
