package com.ecaservice.core.lock.aspect;

import com.ecaservice.common.web.expression.SpelExpressionHelper;
import com.ecaservice.core.lock.annotation.Locked;
import com.ecaservice.core.lock.annotation.TryLocked;
import com.ecaservice.core.lock.fallback.FallbackHandler;
import com.ecaservice.core.lock.service.LockService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.integration.support.locks.LockRegistry;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

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

    private final SpelExpressionHelper spelExpressionHelper = new SpelExpressionHelper();

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
        String lockKey = getLockKey(joinPoint, locked.lockName(), locked.key());
        LockRegistry lockRegistry = applicationContext.getBean(locked.lockRegistry(), LockRegistry.class);
        LockService lockService = new LockService(lockRegistry);
        try {
            lockService.lock(lockKey);
            Object result = joinPoint.proceed();
            lockService.unlock(lockKey);
            return result;
        } catch (CannotAcquireLockException ex) {
            log.error("Acquire lock error: {}", ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            lockService.unlock(lockKey);
            throw ex;
        }
    }

    /**
     * Wrapper for service method to perform try lock.
     *
     * @param joinPoint - give reflective access to the processed method
     * @param tryLocked - try locked annotation
     */
    @Around("execution(@com.ecaservice.core.lock.annotation.TryLocked * * (..)) && @annotation(tryLocked)")
    public void around(ProceedingJoinPoint joinPoint, TryLocked tryLocked) throws Throwable {
        String lockKey = getLockKey(joinPoint, tryLocked.lockName(), tryLocked.key());
        LockRegistry lockRegistry = applicationContext.getBean(tryLocked.lockRegistry(), LockRegistry.class);
        LockService lockService = new LockService(lockRegistry);
        try {
            if (!lockService.tryLock(lockKey)) {
                FallbackHandler fallbackHandler =
                        applicationContext.getBean(tryLocked.fallback(), FallbackHandler.class);
                fallbackHandler.fallback(lockKey);
            } else {
                joinPoint.proceed();
                lockService.unlock(lockKey);
            }
        } catch (CannotAcquireLockException ex) {
            log.error("Acquire lock error: {}", ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            log.error("There was an error: {}", ex.getMessage());
            lockService.unlock(lockKey);
            throw ex;
        }
    }

    private String getLockKey(ProceedingJoinPoint joinPoint, String lockName, String lockKey) {
        if (!StringUtils.hasText(lockKey)) {
            return lockName;
        } else {
            Object value = spelExpressionHelper.parseExpression(joinPoint, lockKey);
            return String.format(LOCK_KEY_FORMAT, lockName, value);
        }
    }
}
