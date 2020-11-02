package com.ecaservice.external.api.service;

import com.ecaservice.external.api.config.ExternalApiConfig;
import com.ecaservice.external.api.dto.EvaluationResponseDto;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import reactor.core.publisher.MonoSink;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.Duration;
import java.util.Optional;

/**
 * Message correlation service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class MessageCorrelationService {

    private final ExternalApiConfig externalApiConfig;

    private Cache<String, MonoSink<EvaluationResponseDto>> messagesCache;

    /**
     * Initialize cache.
     */
    @PostConstruct
    public void initialize() {
        this.messagesCache = CacheBuilder.newBuilder()
                .expireAfterWrite(Duration.ofMinutes(externalApiConfig.getRequestCacheDurationMinutes()))
                .build();
    }

    /**
     * Push response sink.
     *
     * @param correlationId - correlation id
     * @param sink          - response sink
     */
    public void push(@NotBlank String correlationId, @NotNull MonoSink<EvaluationResponseDto> sink) {
        messagesCache.put(correlationId, sink);
    }

    /**
     * Pop response sink.
     *
     * @param correlationId - correlation id
     * @return response sink optional wrapper
     */
    public Optional<MonoSink<EvaluationResponseDto>> pop(@NotBlank String correlationId) {
        MonoSink<EvaluationResponseDto> sink = messagesCache.getIfPresent(correlationId);
        if (sink == null) {
            log.warn("Can't retrieve sink with correlation id [{}]", correlationId);
        } else {
            messagesCache.invalidate(correlationId);
        }
        return Optional.ofNullable(sink);
    }
}
