package com.ecaservice.repository;

import com.ecaservice.model.entity.Experiment;
import com.ecaservice.model.entity.ExperimentResultsEntity;
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
     * Gets experiment results count for specified experiment.
     *
     * @param experiment - experiment entity
     * @return experiment results count
     */
    long countByExperiment(Experiment experiment);

    /**
     * Gets sent experiment results count for specified experiment.
     *
     * @param experiment - experiment entity
     * @return sent experiment results count
     */
    @Query("select count(er) from ExperimentResultsEntity er join ExperimentResultsRequest err " +
            "on err.experimentResults = er where er.experiment = :experiment and err.responseStatus = 'SUCCESS'")
    long getSentResultsCount(@Param("experiment") Experiment experiment);

    /**
     * Finds experiments results for sending to ERS service.
     *
     * @return experiments results list
     */
    @Query("select er from ExperimentResultsEntity er join er.experiment exp where " +
            "exp.experimentStatus = 'FINISHED' and exp.deletedDate is null " +
            "and (select count(err) from ExperimentResultsRequest err where " +
            "err.experimentResults = er and err.responseStatus = 'SUCCESS') = 0")
    List<ExperimentResultsEntity> findExperimentsResultsToErsSent();

    /**
     * Finds experiments results for sending to ERS service.
     *
     * @param experiment - experiment entity
     * @return experiments results list
     */
    @Query("select er from ExperimentResultsEntity er where er.experiment = :experiment " +
            "and (select count(err) from ExperimentResultsRequest err where " +
            "err.experimentResults = er and err.responseStatus = 'SUCCESS') = 0")
    List<ExperimentResultsEntity> findExperimentsResultsToErsSent(@Param("experiment") Experiment experiment);

    /**
     * Finds experiment results ids successfully sent to ERS service.
     *
     * @param ids - experiment results ids list
     * @return experiment results ids
     */
    @Query("select er.id from ExperimentResultsEntity er join ExperimentResultsRequest err " +
            "on err.experimentResults = er where er.id in (:ids) and err.responseStatus = 'SUCCESS'")
    List<Long> findSentResultsIds(@Param("ids") Collection<Long> ids);
}
