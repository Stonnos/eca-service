package com.ecaservice.core.audit.repository;

import com.ecaservice.core.audit.entity.AuditEventRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;

/**
 * Repository to manage with {@link AuditEventRequestEntity}.
 *
 * @author Roman Batygin
 */
public interface AuditEventRequestRepository extends JpaRepository<AuditEventRequestEntity, Long> {

    /**
     * Gets not sent audit events.
     *
     * @return audit events ids
     */
    @Query("select aer.id from AuditEventRequestEntity aer where aer.eventStatus = 'NOT_SENT' order by aer.eventDate")
    List<Long> findNotSentEvents();

    /**
     * Finds audit events requests page with specified ids.
     *
     * @param ids - ids list
     * @return audit events requests page
     */
    List<AuditEventRequestEntity> findByIdIn(Collection<Long> ids);
}
