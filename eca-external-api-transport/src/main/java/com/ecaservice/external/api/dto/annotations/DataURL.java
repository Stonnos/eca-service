package com.ecaservice.external.api.dto.annotations;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The annotated element must be valid data url.
 *
 * @author Roman Batygin
 */
@Documented
@Constraint(validatedBy = {})
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface DataURL {

    String message() default "{data.url.constraint.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}