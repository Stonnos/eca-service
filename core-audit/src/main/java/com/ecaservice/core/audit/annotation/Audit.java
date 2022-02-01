package com.ecaservice.core.audit.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotated method must be audited.
 *
 * @author Roman Batygin
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Audit {

    /**
     * Gets audit code.
     *
     * @return audit code
     */
    String value() default "";

    /**
     * Spring Expression Language (SpEL) attribute for computing the initiator key dynamically.
     * This expression is evaluated after the method has been called and can therefore refer to the {@code result}.
     */
    String targetInitiator() default "";

    /**
     * Spring Expression Language (SpEL) expression for computing the correlation id key dynamically.
     * <p>Default is {@code ""}, meaning all method parameters are considered as a key
     */
    String sourceCorrelationIdKey() default "";

    /**
     * Spring Expression Language (SpEL) attribute for computing the initiator correlation id key dynamically.
     * This expression is evaluated after the method has been called and can therefore refer to the {@code result}.
     */
    String targetCorrelationIdKey() default "";
}
