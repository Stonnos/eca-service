package com.ecaservice.core.mail.client.repository;

import com.ecaservice.core.mail.client.entity.EmailRequestEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

/**
 * Repository to manage with {@link EmailRequestEntity} entity.
 *
 * @author Roman Batygin
 */
public interface EmailRequestRepository extends JpaRepository<EmailRequestEntity, Long> {

    /**
     * Gets not sent email requests.
     *
     * @return email requests ids
     */
    @Query("select emr.id from EmailRequestEntity emr where emr.requestStatus = 'NOT_SENT' and " +
            "(emr.expiredAt is null or emr.expiredAt > :date) order by emr.created")
    List<Long> findNotSentEmailRequests(@Param("date") LocalDateTime date);

    /**
     * Finds email requests page with specified ids.
     *
     * @param ids      - ids list
     * @param pageable - pageable object
     * @return email requests page
     */
    Page<EmailRequestEntity> findByIdIn(Collection<Long> ids, Pageable pageable);
}
