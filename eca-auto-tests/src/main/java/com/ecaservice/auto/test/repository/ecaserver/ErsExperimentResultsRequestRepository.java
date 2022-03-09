package com.ecaservice.auto.test.repository.ecaserver;

import com.ecaservice.auto.test.entity.ecaserver.ErsExperimentResultsRequest;
import com.ecaservice.auto.test.entity.ecaserver.ExperimentResultsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repository to manage with {@link ErsExperimentResultsRequest} persistence entity.
 *
 * @author Roman Batygin
 */
public interface ErsExperimentResultsRequestRepository extends JpaRepository<ErsExperimentResultsRequest, Long> {

    /**
     * Gets ERS experiment results requests.
     *
     * @param experimentResults - experiment results
     * @return ERS experiment results requests
     */
    List<ErsExperimentResultsRequest> findByExperimentResultsIn(List<ExperimentResultsEntity> experimentResults);
}
