package com.ecaservice.repository;

import com.ecaservice.model.entity.ErsResponseStatus;
import com.ecaservice.model.entity.ExperimentResultsEntity;
import com.ecaservice.model.entity.ExperimentResultsRequest;
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
     * @param responseStatus    - response status
     * @return experiment results request
     */
    ExperimentResultsRequest findByExperimentResultsAndResponseStatusEquals(ExperimentResultsEntity experimentResults,
                                                                            ErsResponseStatus responseStatus);
}
