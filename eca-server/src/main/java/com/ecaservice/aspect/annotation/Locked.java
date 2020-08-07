package com.ecaservice.aspect.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotated method must be locked.
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
     * Gets lock expiration in millis.
     *
     * @return lock expiration in millis
     */
    long expiration() default -1L;

    /**
     * Gets waiting lock timeout in millis.
     *
     * @return waiting lock timeout in millis
     */
    long timeout() default -1L;

    /**
     * Gets interval in millis for next attempt locking.
     *
     * @return retry interval
     */
    long retry() default 1000L;
}
