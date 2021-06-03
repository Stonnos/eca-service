package com.ecaservice.core.lock.aspect;

import com.ecaservice.core.lock.annotation.Locked;
import com.ecaservice.core.lock.service.LockService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.integration.support.locks.LockRegistry;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.stream.IntStream;

/**
 * Aspect for lock execution.
 *
 * @author Roman Batygin
 */
@Slf4j
@Aspect
@Component
@ConditionalOnProperty(value = "lock.enabled", havingValue = "true")
public class LockExecutionAspect {

    private static final String LOCK_KEY_FORMAT = "%s-%s";

    private final ExpressionParser expressionParser = new SpelExpressionParser();

    private final ApplicationContext applicationContext;

    /**
     * Constructor with spring dependency injection.
     *
     * @param applicationContext - spring application context bean
     */
    public LockExecutionAspect(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    /**
     * Wrapper for service method to perform lock.
     *
     * @param joinPoint - give reflective access to the processed method
     * @param locked    - locked annotation
     * @return result object
     */
    @Around("execution(@com.ecaservice.core.lock.annotation.Locked * * (..)) && @annotation(locked)")
    public Object around(ProceedingJoinPoint joinPoint, Locked locked) throws Throwable {
        String lockKey = getLockKey(joinPoint, locked);
        LockRegistry lockRegistry = applicationContext.getBean(locked.lockRegistry(), LockRegistry.class);
        LockService lockService = new LockService(lockRegistry);
        try {
            lockService.lock(lockKey);
            return joinPoint.proceed();
        } finally {
            lockService.unlock(lockKey);
        }
    }

    private String getLockKey(ProceedingJoinPoint joinPoint, Locked locked) {
        String key = locked.key();
        if (!StringUtils.hasText(key)) {
            return locked.lockName();
        } else {
            StandardEvaluationContext context = new StandardEvaluationContext();
            MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
            String[] methodParameters = methodSignature.getParameterNames();
            Object[] args = joinPoint.getArgs();
            IntStream.range(0, methodParameters.length).forEach(i -> context.setVariable(methodParameters[i], args[i]));
            Object value = expressionParser.parseExpression(key).getValue(context, Object.class);
            return String.format(LOCK_KEY_FORMAT, locked.lockName(), value);
        }
    }
}
