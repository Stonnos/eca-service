package com.ecaservice.core.audit.aspect;

import com.ecaservice.audit.dto.EventType;
import com.ecaservice.common.web.expression.SpelExpressionHelper;
import com.ecaservice.core.audit.annotation.Audit;
import com.ecaservice.core.audit.annotation.Audits;
import com.ecaservice.core.audit.event.AuditEvent;
import com.ecaservice.core.audit.model.AuditContextParams;
import com.ecaservice.core.audit.service.AuditEventInitiator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;
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
@RequiredArgsConstructor
public class AuditAspect {

    private static final String RESULT_EXPRESSION_PREFIX = "#result";

    private final ApplicationEventPublisher applicationEventPublisher;
    private final AuditEventInitiator auditEventInitiator;
    private final SpelExpressionHelper spelExpressionHelper = new SpelExpressionHelper();

    /**
     * Wrapper to audit service method.
     *
     * @param joinPoint - give reflective access to the processed method
     * @param audit     - audit annotation
     * @return result object
     */
    @Around("execution(@com.ecaservice.core.audit.annotation.Audit * * (..)) && @annotation(audit)")
    public Object around(ProceedingJoinPoint joinPoint, Audit audit) throws Throwable {
        log.debug("Starting to around audited method [{}]", joinPoint.getSignature().getName());
        Object result = joinPoint.proceed();
        publishAuditEvent(audit, joinPoint, result);
        log.debug("Around audited method [{}] has been processed", joinPoint.getSignature().getName());
        return result;
    }

    /**
     * Wrapper to audit service method.
     *
     * @param joinPoint - give reflective access to the processed method
     * @param audits    - audits annotation
     * @return result object
     */
    @Around("execution(@com.ecaservice.core.audit.annotation.Audits * * (..)) && @annotation(audits)")
    public Object around(ProceedingJoinPoint joinPoint, Audits audits) throws Throwable {
        log.debug("Starting to around audited method [{}]", joinPoint.getSignature().getName());
        Object result = joinPoint.proceed();
        for (Audit audit : audits.value()) {
            publishAuditEvent(audit, joinPoint, result);
        }
        log.debug("Around audited method [{}] has been processed", joinPoint.getSignature().getName());
        return result;
    }

    private void publishAuditEvent(Audit audit, ProceedingJoinPoint joinPoint, Object result) {
        Map<String, Object> methodParams = getMethodParams(joinPoint);
        AuditContextParams auditContextParams = new AuditContextParams(methodParams, result);
        String eventInitiator = getInitiator(audit, joinPoint, result);
        String correlationId = getCorrelationId(audit, joinPoint, result);
        AuditEvent auditEvent = new AuditEvent(this, audit.value(), EventType.SUCCESS, correlationId,
                eventInitiator, auditContextParams);
        applicationEventPublisher.publishEvent(auditEvent);
    }

    private String getInitiator(Audit audit, ProceedingJoinPoint joinPoint, Object methodResult) {
        if (StringUtils.isNotBlank(audit.initiatorKey())) {
            return parseExpression(audit.initiatorKey(), joinPoint, methodResult);
        } else {
            return auditEventInitiator.getInitiator();
        }
    }

    private String getCorrelationId(Audit audit, ProceedingJoinPoint joinPoint, Object methodResult) {
        if (StringUtils.isBlank(audit.correlationIdKey())) {
            return null;
        } else {
            return parseExpression(audit.correlationIdKey(), joinPoint, methodResult);
        }
    }

    private String parseExpression(String expression, ProceedingJoinPoint joinPoint, Object methodResult) {
        if (StringUtils.startsWith(expression, RESULT_EXPRESSION_PREFIX)) {
            return parseMethodResultExpression(expression, methodResult);
        } else {
            return parseMethodParameterExpression(expression, joinPoint);
        }
    }

    private String parseMethodParameterExpression(String expression, ProceedingJoinPoint joinPoint) {
        Object value = spelExpressionHelper.parseExpression(joinPoint, expression);
        return String.valueOf(value);
    }

    private String parseMethodResultExpression(String expression, Object methodResult) {
        if (RESULT_EXPRESSION_PREFIX.equals(expression)) {
            return String.valueOf(methodResult);
        } else {
            String expr = StringUtils.substringAfter(expression, String.format("%s.", RESULT_EXPRESSION_PREFIX));
            Object val = spelExpressionHelper.parseExpression(methodResult, expr);
            return String.valueOf(val);
        }
    }

    private Map<String, Object> getMethodParams(ProceedingJoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Map<String, Object> paramsMap = newHashMap();
        String[] parameterNames = methodSignature.getParameterNames();
        if (parameterNames == null || parameterNames.length == 0) {
            return Collections.emptyMap();
        }
        Object[] args = joinPoint.getArgs();
        IntStream.range(0, args.length).forEach(i -> paramsMap.put(parameterNames[i], args[i]));
        return paramsMap;
    }
}
