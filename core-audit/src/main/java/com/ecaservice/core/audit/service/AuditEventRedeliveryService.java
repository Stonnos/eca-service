package com.ecaservice.core.audit.service;

import com.ecaservice.core.audit.config.AuditProperties;
import com.ecaservice.core.audit.entity.AuditEventRequestEntity;
import com.ecaservice.core.audit.mapping.AuditMapper;
import com.ecaservice.core.audit.repository.AuditEventRequestRepository;
import com.ecaservice.core.lock.annotation.TryLocked;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import static com.ecaservice.core.audit.config.AuditCoreConfiguration.AUDIT_LOCK_REGISTRY;

/**
 * Audit redelivery service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@ConditionalOnProperty(value = "audit.redelivery", havingValue = "true")
@RequiredArgsConstructor
public class AuditEventRedeliveryService {

    private final AuditProperties auditProperties;
    private final AuditMapper auditMapper;
    private final AuditEventSender auditEventSender;
    private final AuditEventRequestRepository auditEventRequestRepository;

    /**
     * Redeliver not sent audit events.
     */
    @TryLocked(lockName = "processNotSentAuditEvents", lockRegistry = AUDIT_LOCK_REGISTRY)
    public void processNotSentEvents() {
        log.debug("Starting redeliver audit events");
        var eventIds = auditEventRequestRepository.findNotSentEvents();
        if (!CollectionUtils.isEmpty(eventIds)) {
            log.info("Found [{}] not sent audit events", eventIds.size());
            Pageable pageRequest = PageRequest.of(0, auditProperties.getPageSize());
            Page<AuditEventRequestEntity> page;
            do {
                page = auditEventRequestRepository.findByIdIn(eventIds, pageRequest);
                if (page == null || !page.hasContent()) {
                    log.trace("No one audit event has been fetched");
                    break;
                } else {
                    page.forEach(auditEventRequestEntity -> {
                        var auditEventRequest = auditMapper.map(auditEventRequestEntity);
                        auditEventSender.sendAuditEvent(auditEventRequest, auditEventRequestEntity);
                    });
                }
                pageRequest = page.nextPageable();
            } while (page.hasNext());
        }
        log.debug("Redeliver audit events has been finished");
    }
}
