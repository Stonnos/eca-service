package com.ecaservice.repository;

import com.ecaservice.dto.evaluation.ResponseStatus;
import com.ecaservice.model.entity.ClassifierOptionsRequestModel;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

/**
 * Repository to manage with {@link ClassifierOptionsRequestModel} entity.
 *
 * @author Roman Batygin
 */
public interface ClassifierOptionsRequestModelRepository extends JpaRepository<ClassifierOptionsRequestModel, Long> {

    /**
     * Finds last requests by specified params.
     *
     * @param dataMd5Hash      - training data MD5 hash
     * @param responseStatuses - response statuses
     * @param requestDate      - request date bound
     * @param pageable         - pageable object
     * @return classifier options request models
     */
    @Query("select req from ClassifierOptionsRequestModel req where req.dataMd5Hash = :dataMd5Hash " +
            "and req.responseStatus in (:responseStatuses) and req.requestDate > :requestDate " +
            "order by req.requestDate desc")
    List<ClassifierOptionsRequestModel> findLastRequests(@Param("dataMd5Hash") String dataMd5Hash,
                                                         @Param("responseStatuses")
                                                                 Collection<ResponseStatus> responseStatuses,
                                                         @Param("requestDate") LocalDateTime requestDate,
                                                         Pageable pageable);
}
