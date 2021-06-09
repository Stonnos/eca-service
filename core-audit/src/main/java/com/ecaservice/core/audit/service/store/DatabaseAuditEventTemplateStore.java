package com.ecaservice.core.audit.service.store;

import com.ecaservice.core.audit.entity.AuditCodeEntity;
import com.ecaservice.core.audit.entity.EventType;
import com.ecaservice.core.audit.exception.AuditDataNotFoundException;
import com.ecaservice.core.audit.mapping.AuditMapper;
import com.ecaservice.core.audit.model.AuditEventTemplateModel;
import com.ecaservice.core.audit.repository.AuditCodeRepository;
import com.ecaservice.core.audit.repository.AuditEventTemplateRepository;
import com.ecaservice.core.audit.service.AuditEventTemplateStore;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * Service to loading audit event templates from database.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DatabaseAuditEventTemplateStore implements AuditEventTemplateStore {

    private final AuditMapper auditMapper;
    private final AuditCodeRepository auditCodeRepository;
    private final AuditEventTemplateRepository auditEventTemplateRepository;

    private LoadingCache<CacheKey, AuditEventTemplateModel> eventTemplatesCache;

    /**
     * Cache key model.
     */
    @Data
    @AllArgsConstructor
    private static class CacheKey {
        /**
         * Audit code
         */
        String auditCode;
        /**
         * Event type
         */
        EventType eventType;
    }

    /**
     * Initialize cache.
     */
    @PostConstruct
    public void initializeCache() {
        log.info("Starting to initialize audit templates cache");
        this.eventTemplatesCache = CacheBuilder.newBuilder()
                .build(createCacheLoader());
        log.info("Audit templates cache has been initialized");
    }

    @Override
    public AuditEventTemplateModel getAuditEventTemplate(String auditCode, EventType eventType) {
        log.debug("Gets audit event with code [{}], type [{}]", auditCode, eventType);
        return eventTemplatesCache.getUnchecked(new CacheKey(auditCode, eventType));
    }

    private CacheLoader<CacheKey, AuditEventTemplateModel> createCacheLoader() {
        return new CacheLoader<>() {
            @Override
            public AuditEventTemplateModel load(CacheKey key) {
                var auditCodeEntity = getAuditCode(key.getAuditCode());
                var auditCodeTemplate = auditEventTemplateRepository
                        .findByAuditCodeAndEventType(auditCodeEntity, key.getEventType())
                        .orElseThrow(() -> new AuditDataNotFoundException(String.format("Audit event with code [%s], type [%s] not found", key.getAuditCode(), key.getEventType())));
                log.debug("Fetched audit event template for key {}", key);
                return auditMapper.map(auditCodeTemplate);
            }
        };
    }

    private AuditCodeEntity getAuditCode(String code) {
        return auditCodeRepository.findById(code)
                .orElseThrow(() -> new AuditDataNotFoundException(String.format("Audit code [%s] not found", code)));
    }
}
