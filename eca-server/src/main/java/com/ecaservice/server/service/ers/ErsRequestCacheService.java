package com.ecaservice.server.service.ers;

import com.ecaservice.common.web.exception.EntityNotFoundException;
import com.ecaservice.server.model.entity.ErsRequest;
import com.ecaservice.server.model.entity.ErsRequestCache;
import com.ecaservice.server.repository.ErsRequestCacheRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Ers request cache service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ErsRequestCacheService {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final ErsRequestCacheRepository ersRequestCacheRepository;

    /**
     * Puts ers request into cache.
     *
     * @param ersRequest  - ers request
     * @param jsonRequest - json request string
     */
    public void put(ErsRequest ersRequest, String jsonRequest) {
        log.info("Starting to put ers request [{}] into cache", ersRequest.getRequestId());
        ErsRequestCache ersRequestCache = new ErsRequestCache();
        ersRequestCache.setJsonRequest(jsonRequest);
        ersRequestCache.setErsRequest(ersRequest);
        ersRequestCache.setCreated(LocalDateTime.now());
        ersRequestCacheRepository.save(ersRequestCache);
        log.info("Ers request [{}] has been put into cache", ersRequest.getRequestId());
    }

    /**
     * Evicts ers request from cache.
     *
     * @param ersRequest - ers request
     */
    public void evict(ErsRequest ersRequest) {
        log.info("Starting to remove ers request [{}] from cache", ersRequest.getRequestId());
        var ersRequestCacheId = ersRequestCacheRepository.findRequestCacheId(ersRequest)
                .orElseThrow(() -> new EntityNotFoundException(ErsRequestCache.class, ersRequest.getRequestId()));
        ersRequestCacheRepository.deleteById(ersRequestCacheId);
        log.info("Ers request [{}] has been removed from cache", ersRequest.getRequestId());
    }

    /**
     * Checks ers request cache.
     *
     * @param ersRequest - ers request
     * @return {@code true} if ers request cache exists, {@code false} otherwise
     */
    public boolean contains(ErsRequest ersRequest) {
        log.debug("Check ers request [{}] in cache", ersRequest.getRequestId());
        return ersRequestCacheRepository.existsByErsRequest(ersRequest);
    }

    /**
     * Evicts ers request from cache if exists.
     *
     * @param ersRequest - ers request
     */
    public void evictIfAbsent(ErsRequest ersRequest) {
        if (contains(ersRequest)) {
            evict(ersRequest);
        }
    }

    /**
     * Puts ers request into cache if absent
     *
     * @param ersRequest - ers request
     * @param request    - request object
     */
    public void putIfAbsent(ErsRequest ersRequest, Object request) {
        if (contains(ersRequest)) {
            log.info("Ers request [{}] is already in cache. Skipped...", ersRequest.getRequestId());
        } else {
            try {
                String jsonRequest = OBJECT_MAPPER.writeValueAsString(request);
                put(ersRequest, jsonRequest);
            } catch (Exception ex) {
                log.error("There was an error while cache put ers request [{}]: {}", ersRequest.getRequestId(),
                        ex.getMessage());
            }
        }
    }
}
