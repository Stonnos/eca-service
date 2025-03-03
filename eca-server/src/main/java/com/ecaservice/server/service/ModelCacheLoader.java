package com.ecaservice.server.service;

import com.ecaservice.s3.client.minio.service.ObjectStorageService;
import com.google.common.cache.CacheLoader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Models cache loader.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ModelCacheLoader extends CacheLoader<String, Object> {

    public final ObjectStorageService objectStorageService;

    @Override
    public Object load(String key) {
        try {
            return objectStorageService.getObject(key, Object.class);
        } catch (IOException | ClassNotFoundException ex) {
            log.error("Error while load model {}: {}", key, ex.getMessage());
            throw new IllegalStateException(ex);
        }
    }
}
