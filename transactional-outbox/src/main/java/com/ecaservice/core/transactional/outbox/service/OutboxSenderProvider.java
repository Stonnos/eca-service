package com.ecaservice.core.transactional.outbox.service;

import com.ecaservice.core.transactional.outbox.annotation.OutboxSender;
import com.ecaservice.core.transactional.outbox.annotation.TransactionalOutbox;
import com.ecaservice.core.transactional.outbox.model.MethodInfo;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Outbox sender provider.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OutboxSenderProvider {

    private final ApplicationContext applicationContext;

    private final Map<String, MethodInfo> outboxSendersCache = new ConcurrentHashMap<>(256);

    /**
     * Initializes outbox senders cache.
     */
    @PostConstruct
    public void initOutboxSendersCache() {
        log.info("Starting to initialize outbox senders cache");
        var beans = applicationContext.getBeansWithAnnotation(TransactionalOutbox.class);
        if (CollectionUtils.isEmpty(beans)) {
            log.info("No one bean annotated with [{}] found. Skipped...", TransactionalOutbox.class.getSimpleName());
        } else {
            List<MethodInfo> outboxSenderMethods = getAllOutboxSenderMethods(beans);
            log.info("Found [{}] outbox sender methods", outboxSenderMethods.size());
            for (MethodInfo methodInfo : outboxSenderMethods) {
                Method method = methodInfo.getMethod();
                var senderAnnotation = AnnotationUtils.findAnnotation(methodInfo.getMethod(), OutboxSender.class);
                if (outboxSendersCache.containsKey(senderAnnotation.value())) {
                    throw new IllegalArgumentException(
                            String.format("Found duplicate code [%s] for annotation [%s]. Codes must be unique",
                                    senderAnnotation.value(), OutboxSender.class.getSimpleName()));
                }
                if (method.getParameters().length != 1) {
                    throw new IllegalStateException(
                            String.format("[%s#%s] annotated with [%s], code [%s] must have only one argument",
                                    method.getDeclaringClass().getSimpleName(), method.getName(),
                                    OutboxSender.class.getSimpleName(), senderAnnotation.value()));
                }
                outboxSendersCache.put(senderAnnotation.value(), methodInfo);
            }
        }
        log.info("Outbox senders cache has been initialized");
    }

    /**
     * Gets outbox sender method info.
     *
     * @param messageCode - message code
     * @return outbox sender method info
     */
    public MethodInfo getOutboxSender(String messageCode) {
        return outboxSendersCache.get(messageCode);
    }

    private List<MethodInfo> getAllOutboxSenderMethods(Map<String, Object> beans) {
        return beans.entrySet().stream()
                .flatMap(entry -> {
                    var methods = ReflectionUtils.getDeclaredMethods(entry.getValue().getClass());
                    var outboxSenderMethods = getOutboxSenderMethods(methods);
                    return outboxSenderMethods.stream()
                            .map(method -> new MethodInfo(entry.getValue(), method));
                })
                .collect(Collectors.toList());
    }

    private List<Method> getOutboxSenderMethods(Method[] methods) {
        return Stream.of(methods)
                .filter(method -> AnnotationUtils.findAnnotation(method, OutboxSender.class) != null)
                .collect(Collectors.toList());
    }
}
