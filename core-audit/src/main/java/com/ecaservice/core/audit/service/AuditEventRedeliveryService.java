package com.ecaservice.core.audit.service;

import com.ecaservice.core.audit.config.AuditProperties;
import com.ecaservice.core.audit.mapping.AuditMapper;
import com.ecaservice.core.audit.repository.AuditEventRequestRepository;
import com.ecaservice.core.lock.annotation.TryLocked;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import static com.ecaservice.common.web.util.PageHelper.processWithPagination;
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
            processWithPagination(eventIds, auditEventRequestRepository::findByIdIn,
                    pageContent -> pageContent.forEach(auditEventRequestEntity -> {
                        var auditEventRequest = auditMapper.map(auditEventRequestEntity);
                        auditEventSender.sendAuditEvent(auditEventRequest, auditEventRequestEntity);
                    }), auditProperties.getPageSize()
            );
        }
        log.debug("Redeliver audit events has been finished");
    }
}
