package com.ecaservice.core.audit.aspect;

import com.ecaservice.audit.dto.EventType;
import com.ecaservice.core.audit.annotation.Audit;
import com.ecaservice.core.audit.model.AuditContextParams;
import com.ecaservice.core.audit.service.AuditEventService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.stream.IntStream;

import static com.google.common.collect.Maps.newHashMap;

/**
 * Aspect for audit execution.
 *
 * @author Roman Batygin
 */
@Slf4j
@Aspect
@Component
public class AuditAspect {

    private final AuditEventService auditEventService;

    private final ParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();

    /**
     * Constructor with spring dependency injection.
     *
     * @param auditEventService - audit event service
     */
    public AuditAspect(AuditEventService auditEventService) {
        this.auditEventService = auditEventService;
    }

    /**
     * Wrapper to audit service method.
     *
     * @param joinPoint - give reflective access to the processed method
     * @param audit     - audit annotation
     * @return result object
     */
    @Around("execution(@com.ecaservice.core.audit.annotation.Audit * * (..)) && @annotation(audit)")
    public Object around(ProceedingJoinPoint joinPoint, Audit audit) throws Throwable {
        String eventId = UUID.randomUUID().toString();
        log.debug("Starting to around audited method [{}] with event id [{}]", joinPoint.getSignature().getName(),
                eventId);
        Map<String, Object> methodParams = getMethodParams(joinPoint);
        Object result = joinPoint.proceed();
        AuditContextParams auditContextParams = new AuditContextParams(methodParams, result);
        auditEventService.audit(eventId, audit.value(), EventType.SUCCESS, auditContextParams);
        log.debug("Around audited method [{}] with event id [{}] has been processed",
                joinPoint.getSignature().getName(), eventId);
        return result;
    }

    private Map<String, Object> getMethodParams(ProceedingJoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        Map<String, Object> paramsMap = newHashMap();
        String[] parameterNames = parameterNameDiscoverer.getParameterNames(method);
        if (parameterNames == null || parameterNames.length == 0) {
            return Collections.emptyMap();
        }
        Object[] args = joinPoint.getArgs();
        IntStream.range(0, args.length).forEach(i -> paramsMap.put(parameterNames[i], args[i]));
        return paramsMap;
    }
}
