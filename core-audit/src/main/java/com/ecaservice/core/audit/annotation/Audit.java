package com.ecaservice.core.audit.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotated method must be audited.
 *
 * @author Roman Batygin
 */
@Repeatable(Audits.class)
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
     * Spring Expression Language (SpEL) attribute for computing the initiator key dynamically from method
     * parameters.
     */
    String sourceInitiator() default "";

    /**
     * Spring Expression Language (SpEL) attribute for computing the initiator key dynamically.
     * This expression is evaluated after the method has been called and can therefore refer to the {@code result}.
     */
    String targetInitiator() default "";

    /**
     * Spring Expression Language (SpEL) expression for computing the correlation id key dynamically from method
     * parameters.
     */
    String sourceCorrelationIdKey() default "";

    /**
     * Spring Expression Language (SpEL) attribute for computing the initiator correlation id key dynamically.
     * This expression is evaluated after the method has been called and can therefore refer to the {@code result}.
     */
    String targetCorrelationIdKey() default "";
}
