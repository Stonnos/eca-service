package com.ecaservice.server.repository;

import com.ecaservice.server.model.entity.ClassifierOptionsRequestEntity;
import com.ecaservice.server.model.entity.ErsResponseStatus;
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
     * @param dataUuid         - training data uuid
     * @param responseStatuses - response statuses
     * @param requestDate      - request date bound
     * @param pageable         - pageable object
     * @return classifier options request list
     */
    @Query("select req from ClassifierOptionsRequestEntity req join req.classifierOptionsRequestModel cm " +
            "where cm.instancesInfo.uuid = :dataUuid and cm.responseStatus in (:responseStatuses) and " +
            "cm.requestDate > :requestDate order by req.creationDate desc")
    List<ClassifierOptionsRequestEntity> findLastRequests(@Param("dataUuid") String dataUuid,
                                                          @Param("responseStatuses")
                                                          Collection<ErsResponseStatus> responseStatuses,
                                                          @Param("requestDate") LocalDateTime requestDate,
                                                          Pageable pageable);
}
