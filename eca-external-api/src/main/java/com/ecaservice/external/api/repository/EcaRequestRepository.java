package com.ecaservice.external.api.repository;

import com.ecaservice.external.api.entity.EcaRequestEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

/**
 * Repository to manage with {@link EcaRequestEntity} persistence entity.
 *
 * @author Roman Batygin
 */
public interface EcaRequestRepository extends JpaRepository<EcaRequestEntity, Long> {

    /**
     * Finds exceeded requests ids.
     *
     * @param pageable - pageable object
     * @param dateTime - date time value
     * @return requests page
     */
    @Query("select er from EcaRequestEntity er where er.requestStage = 'REQUEST_SENT' " +
            "and er.requestTimeoutDate is not null and er.requestTimeoutDate < :dateTime order by er.requestDate")
    Page<EcaRequestEntity> findExceededRequestIds(@Param("dateTime") LocalDateTime dateTime,
                                                  Pageable pageable);
}
