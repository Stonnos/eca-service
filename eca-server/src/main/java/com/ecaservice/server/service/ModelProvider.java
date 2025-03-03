package com.ecaservice.server.service;

import com.ecaservice.server.config.AppProperties;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.LoadingCache;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * Model provider.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ModelProvider {

    public final AppProperties appProperties;
    public final ModelCacheLoader modelCacheLoader;

    private LoadingCache<String, Object> modelsCache;

    /**
     * Initialize cache.
     */
    @PostConstruct
    public void initializeCache() {
        this.modelsCache = CacheBuilder.newBuilder()
                .expireAfterWrite(appProperties.getModelCacheTtlSeconds(), TimeUnit.SECONDS)
                .maximumSize(appProperties.getModelCacheSize())
                .build(modelCacheLoader);
        log.info("Models cache has been initialized");
    }

    /**
     * Load model from object storage.
     *
     * @param modelPath  - model path
     * @param modelClass - model class
     * @param <T>        - model generic type
     * @return model
     */
    public <T> T loadModel(String modelPath, Class<T> modelClass) {
        return modelClass.cast(modelsCache.getUnchecked(modelPath));
    }
}
