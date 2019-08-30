package com.ecaservice.repository;

import com.ecaservice.model.entity.ErsResponseStatus;
import com.ecaservice.model.entity.Experiment;
import com.ecaservice.model.entity.ExperimentResultsEntity;
import com.ecaservice.model.entity.ExperimentResultsRequest;
import org.springframework.data.jpa.repository.JpaRepository;

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
     * Finds experiment results requests with specified status.
     *
     * @param experimentResults - experiment results entity
     * @param responseStatus    - response status
     * @return experiment results request
     */
    ExperimentResultsRequest findByExperimentResultsAndResponseStatusEquals(ExperimentResultsEntity experimentResults,
                                                                            ErsResponseStatus responseStatus);
}
