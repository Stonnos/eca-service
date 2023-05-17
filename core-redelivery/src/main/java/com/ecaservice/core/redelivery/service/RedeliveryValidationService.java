package com.ecaservice.core.redelivery.service;

import com.ecaservice.core.redelivery.annotation.Retry;
import com.ecaservice.core.redelivery.annotation.Retryable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.support.AopUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;

import javax.annotation.PostConstruct;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;

/**
 * Redelivery validation service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RedeliveryValidationService {

    private final ApplicationContext applicationContext;

    /**
     * Checks uniques of all {@link Retry#value()} codes.
     */
    @PostConstruct
    public void validate() {
        log.info("Starting to validate redelivery annotations");
        var beans = applicationContext.getBeansWithAnnotation(Retryable.class);
        if (CollectionUtils.isEmpty(beans)) {
            log.info("No one bean annotated with [{}] found. Skipped...", Retryable.class.getSimpleName());
        } else {
            var retryMethods = getAllRetryMethods(beans);
            log.info("Found [{}] methods annotated with [{}]", retryMethods.size(), Retry.class.getSimpleName());
            Set<String> retryCodes = newHashSet();
            for (var method : retryMethods) {
                var retryAnnotation = AnnotationUtils.findAnnotation(method, Retry.class);
                Assert.notNull(retryAnnotation, "Expected not null retry annotation");
                if (!retryCodes.add(retryAnnotation.value())) {
                    throw new IllegalArgumentException(
                            String.format("Found duplicate code [%s] for annotation [%s]. Codes must be unique",
                                    retryAnnotation.value(), Retry.class.getSimpleName()));
                }
                if (method.getParameters().length == 0) {
                    throw new IllegalStateException(
                            String.format("[%s#%s] annotated with [%s], code [%s], has no input parameters",
                                    method.getDeclaringClass().getSimpleName(), method.getName(),
                                    Retry.class.getSimpleName(), retryAnnotation.value()));
                }
            }
        }
        log.info("Redelivery annotations validation has been passed");
    }

    private List<Method> getAllRetryMethods(Map<String, Object> beans) {
        List<Method> resultMethods = newArrayList();
        for (var entry : beans.entrySet()) {
            Class<?> targetClass = AopUtils.getTargetClass(entry.getValue());
            var methods = ReflectionUtils.getDeclaredMethods(targetClass);
            var retryMethods = getRetryMethods(methods);
            if (!CollectionUtils.isEmpty(retryMethods)) {
                resultMethods.addAll(retryMethods);
            }
        }
        return resultMethods;
    }

    private List<Method> getRetryMethods(Method[] methods) {
        return Stream.of(methods)
                .filter(method -> AnnotationUtils.findAnnotation(method, Retry.class) != null)
                .collect(Collectors.toList());
    }
}
