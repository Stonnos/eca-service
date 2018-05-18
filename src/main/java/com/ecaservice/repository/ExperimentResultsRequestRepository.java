package com.ecaservice.repository;

import com.ecaservice.model.entity.ExperimentResultsRequest;
import com.ecaservice.model.experiment.ExperimentResultsRequestStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;

/**
 * Repository to manage with {@link ExperimentResultsRequest} persistence entity.
 *
 * @author Roman Batygin
 */
public interface ExperimentResultsRequestRepository extends JpaRepository<ExperimentResultsRequest, Long> {

    /**
     * Finds not sent experiments by statuses
     *
     * @param statuses {@link ExperimentResultsRequestStatus} collection
     * @param pageable {@link Pageable} object
     * @return experiments requests list
     */
    @Query("select exp from ExperimentResultsRequest exp where exp.requestStatus in (:statuses) " +
            "and exp.sentDate is null order by exp.creationDate")
    Page<ExperimentResultsRequest> findNotSentExperimentRequests(
            @Param("statuses") Collection<ExperimentResultsRequestStatus> statuses, Pageable pageable);
}
