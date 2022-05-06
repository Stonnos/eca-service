package com.ecaservice.core.lock.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static com.ecaservice.core.lock.config.CoreLockConfiguration.DEFAULT_FALLBACK_HANDLER;

/**
 * Annotated method must be locked.
 *
 * @author Roman Batygin
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Locked {

    /**
     * Gets lock name.
     *
     * @return lock name
     */
    String lockName() default "";

    /**
     * Spring Expression Language (SpEL) attribute for computing the key dynamically.
     */
    String key() default "";

    /**
     * Gets lock registry bean name. Bean must implements
     * {@link org.springframework.integration.support.locks.LockRegistry} interface.
     *
     * @return lock registry bean name
     */
    String lockRegistry() default "redisLockRegistry";

    /**
     * Waits for lock? If the value is true then the thread waits for the monitor to become free.
     *
     * @return wait for lock boolean flag
     */
    boolean waitForLock() default true;

    /**
     * The bean name of the custom {@link com.ecaservice.core.lock.fallback.FallbackHandler}.
     * Fallback call used when {@link Locked#waitForLock()} value is set to false
     *
     * @return fallback class
     */
    String fallback() default DEFAULT_FALLBACK_HANDLER;
}
