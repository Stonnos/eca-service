package com.ecaservice.server.repository;

import com.ecaservice.server.model.entity.ExperimentResultsEntity;
import com.ecaservice.server.model.entity.ExperimentResultsRequest;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository to manage with {@link ExperimentResultsRequest} persistence entity.
 *
 * @author Roman Batygin
 */
public interface ExperimentResultsRequestRepository extends JpaRepository<ExperimentResultsRequest, Long> {

    /**
     * Finds experiment results request with specified status.
     *
     * @param experimentResults - experiment results entity
     * @return experiment results request
     */
    ExperimentResultsRequest findByExperimentResults(ExperimentResultsEntity experimentResults);
}
