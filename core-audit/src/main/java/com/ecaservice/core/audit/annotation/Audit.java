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
     * Spring Expression Language (SpEL) attribute for computing the initiator key dynamically. Expression may contain
     * method input parameter shortcut or method returned value shortcut.
     * Method input parameters examples: {@code #id}, {@code #object.name} - for field in class object.
     * Method returned value examples: {@code #result}, {@code #result.value} - for field in class object.
     * Note that method returned value shortcut should start with #result prefix.
     */
    String initiatorKey() default "";

    /**
     * Spring Expression Language (SpEL) attribute for computing the correlation id key dynamically. Expression may
     * contain method input parameter shortcut or method returned value shortcut.
     * Method input parameters examples: {@code #id}, {@code #object.name} - for field in class object.
     * Method returned value examples: {@code #result}, {@code #result.value} - for field in class object.
     * Note that method returned value shortcut should start with #result prefix.
     */
    String correlationIdKey() default "";
}
