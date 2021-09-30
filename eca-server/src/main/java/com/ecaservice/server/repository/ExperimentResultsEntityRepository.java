package com.ecaservice.server.repository;

import com.ecaservice.server.model.entity.Experiment;
import com.ecaservice.server.model.entity.ExperimentResultsEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Repository to manage with {@link ExperimentResultsEntity} persistence entity.
 *
 * @author Roman Batygin
 */
public interface ExperimentResultsEntityRepository extends JpaRepository<ExperimentResultsEntity, Long> {

    @EntityGraph(value = "classifierInfo", type = EntityGraph.EntityGraphType.FETCH)
    Optional<ExperimentResultsEntity> findById(Long id);

    /**
     * Finds experiment results list by specified experiment.
     *
     * @param experiment - experiment entity
     * @return experiment results list
     */
    @EntityGraph(value = "classifierInfo", type = EntityGraph.EntityGraphType.FETCH)
    List<ExperimentResultsEntity> findByExperimentOrderByResultsIndex(Experiment experiment);

    /**
     * Finds experiments results for sending to ERS service.
     *
     * @return experiments results list
     */
    @Query("select er from ExperimentResultsEntity er join er.experiment exp where " +
            "exp.requestStatus = 'FINISHED' and exp.deletedDate is null " +
            "and (select count(err) from ExperimentResultsRequest err where " +
            "err.experimentResults = er and err.responseStatus = 'SUCCESS') = 0")
    List<ExperimentResultsEntity> findExperimentsResultsToErsSent();

    /**
     * Finds experiment results ids successfully sent to ERS service.
     *
     * @param ids - experiment results ids list
     * @return experiment results ids
     */
    @Query("select distinct er.id from ExperimentResultsEntity er join ExperimentResultsRequest err " +
            "on err.experimentResults = er where er.id in (:ids) and err.responseStatus = 'SUCCESS'")
    List<Long> findSentResultsIds(@Param("ids") Collection<Long> ids);
}
