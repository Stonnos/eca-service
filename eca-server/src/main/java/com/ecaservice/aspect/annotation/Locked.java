package com.ecaservice.aspect.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotated method must be locked.
 *
 * @author Roman Batygin
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Locked {

    long MILLIS_IN_SECOND = 1000L;

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
     * How long (in ms) the lock should be kept in case the machine which obtained the lock died before releasing it.
     * This is just a fallback, under normal circumstances the lock is released as soon the tasks finishes. Negative
     * value means default
     *
     * @return lock expiration in millis
     */
    long expiration() default 60 * MILLIS_IN_SECOND;

    /**
     * Gets waiting lock timeout in millis.
     *
     * @return waiting lock timeout in millis
     */
    long timeout() default 120 * MILLIS_IN_SECOND;

    /**
     * Gets interval in millis for next attempt locking.
     *
     * @return retry interval
     */
    long retry() default 100L;
}
