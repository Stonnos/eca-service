package com.ecaservice.repository;

import com.ecaservice.model.entity.Experiment;
import com.ecaservice.model.entity.ExperimentResultsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

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
    List<ExperimentResultsEntity> findByExperiment(Experiment experiment);

    /**
     * Finds experiments results for sending to ERS service.
     *
     * @return experiments results list
     */
    @Query("select er from ExperimentResultsEntity er join er.experiment exp where " +
            "exp.experimentStatus = 'FINISHED' and exp.deletedDate is null " +
            "and (select count(err) from ExperimentResultsRequest err where " +
            "err.experimentResultsEntity = er and err.responseStatus = 'SUCCESS') = 0")
    List<ExperimentResultsEntity> findfindExperimentsResulsToErsSent();
}
