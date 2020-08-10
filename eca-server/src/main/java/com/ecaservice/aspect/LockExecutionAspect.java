package com.ecaservice.aspect;

import com.ecaservice.aspect.annotation.Locked;
import com.ecaservice.service.evaluation.CalculationExecutorService;
import com.ecaservice.service.lock.LockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

/**
 * Aspect for lock execution.
 *
 * @author Roman Batygin
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class LockExecutionAspect {

    private final LockService lockService;
    private final CalculationExecutorService calculationExecutorService;

    private ExpressionParser expressionParser = new SpelExpressionParser();

    /**
     * Wrapper for service method to perform lock.
     *
     * @param joinPoint - give reflective access to the processed method
     * @param locked    - locked annotation
     * @return result object
     */
    @Around("execution(@com.ecaservice.aspect.annotation.Locked * * (..)) && @annotation(locked)")
    public Object around(ProceedingJoinPoint joinPoint, Locked locked) throws Exception {
        String lockKey = getLockKey(joinPoint, locked);
        lockService.resetLockIfExpired(locked.lockName(), lockKey);
        lockService.waitForLock(locked.lockName(), lockKey, locked.expiration(), locked.timeout(), locked.retry());
        try {
            Callable<Object> callable = getCallable(joinPoint);
            return calculationExecutorService.execute(callable, locked.expiration(), TimeUnit.MILLISECONDS);
        } finally {
            lockService.unlock(locked.lockName(), lockKey);
        }
    }

    private Callable<Object> getCallable(ProceedingJoinPoint joinPoint) {
        return () -> {
            try {
                return joinPoint.proceed();
            } catch (Throwable ex) {
                throw new IllegalStateException(ex);
            }
        };
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
            return String.valueOf(value);
        }
    }
}
