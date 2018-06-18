package com.ecaservice.repository;

import com.ecaservice.dto.evaluation.ResponseStatus;
import com.ecaservice.model.entity.ClassifierOptionsRequestEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

/**
 * Repository to manage with {@link ClassifierOptionsRequestEntity} persistence entity.
 */
public interface ClassifierOptionsRequestRepository extends JpaRepository<ClassifierOptionsRequestEntity, Long> {

    /**
     * Finds last requests by specified params.
     *
     * @param dataMd5Hash      - training data MD5 hash
     * @param responseStatuses - response statuses
     * @param creationDate     - creation date bound
     * @param pageable         - pageable object
     * @return classifier options request models
     */
    @Query("select req from ClassifierOptionsRequestEntity req join req.classifierOptionsRequestModel cm " +
            "where cm.dataMd5Hash = :dataMd5Hash and cm.responseStatus in (:responseStatuses) and " +
            "req.creationDate > :creationDate order by req.creationDate desc")
    List<ClassifierOptionsRequestEntity> findLastRequests(@Param("dataMd5Hash") String dataMd5Hash,
                                                          @Param("responseStatuses")
                                                                  Collection<ResponseStatus> responseStatuses,
                                                          @Param("creationDate") LocalDateTime creationDate,
                                                          Pageable pageable);
}
