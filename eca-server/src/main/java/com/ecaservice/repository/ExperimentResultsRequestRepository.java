package com.ecaservice.repository;

import com.ecaservice.model.entity.ErsResponseStatus;
import com.ecaservice.model.entity.Experiment;
import com.ecaservice.model.entity.ExperimentResultsEntity;
import com.ecaservice.model.entity.ExperimentResultsRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

/**
 * Repository to manage with {@link ExperimentResultsRequest} persistence entity.
 *
 * @author Roman Batygin
 */
public interface ExperimentResultsRequestRepository extends JpaRepository<ExperimentResultsRequest, Long> {

    /**
     * Gets experiment results requests for specified experiment.
     *
     * @param experiment - experiment
     * @return experiment results requests list
     */
    List<ExperimentResultsRequest> findAllByExperiment(Experiment experiment);

    /**
     * Finds experiment results requests with success status.
     *
     * @param experimentResults - experiment results entity
     * @param pageable          - pageable object
     * @return experiment results requests list
     */
    @Query("select err from ExperimentResultsRequest err where err.experimentResults = :experimentResults " +
            "and err.responseStatus = 'SUCCESS'")
    List<ExperimentResultsRequest> findSuccessRequests(
            @Param("experimentResults") ExperimentResultsEntity experimentResults, Pageable pageable);

    /**
     * Checks existing experiment results requests with specified statuses.
     *
     * @param experiment       - experiment entity
     * @param responseStatuses - ERS response statuses
     * @return {@code true} if any requests exists
     */
    boolean existsByExperimentAndResponseStatusIn(Experiment experiment,
                                                  Collection<ErsResponseStatus> responseStatuses);
}
