package com.ecaservice.external.api.repository;

import com.ecaservice.external.api.entity.EcaRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

/**
 * Repository to manage with {@link EcaRequestEntity} persistence entity.
 *
 * @author Roman Batygin
 */
public interface EcaRequestRepository extends JpaRepository<EcaRequestEntity, Long> {

    /**
     * Finds exceeded requests ids.
     *
     * @param dateTime - date time value
     * @return requests ids list
     */
    @Query("select er.id from EcaRequestEntity er where er.requestStage = 'REQUEST_SENT' " +
            "and er.requestTimeoutDate is not null and er.requestTimeoutDate < :dateTime order by er.requestDate")
    List<Long> findExceededRequestIds(@Param("dateTime") LocalDateTime dateTime);

    /**
     * Finds eca requests page by ids.
     *
     * @param ids - ids list
     * @return eca requests page
     */
    List<EcaRequestEntity> findByIdIn(Collection<Long> ids);
}
