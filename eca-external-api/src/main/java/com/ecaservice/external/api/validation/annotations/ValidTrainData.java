package com.ecaservice.external.api.validation.annotations;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The annotated element must be valid train data.
 *
 * @author Roman Batygin
 */
@Documented
@Constraint(validatedBy = {})
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidTrainData {

    String message() default "{data.extension.constraint.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}