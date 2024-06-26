package com.ecaservice.oauth.validation.annotations;

import com.ecaservice.oauth.validation.UniqueLoginValidator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The annotated element must be unique login.
 *
 * @author Roman Batygin
 */
@Documented
@Constraint(validatedBy = UniqueLoginValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueLogin {

    String message() default "{user.login.unique.constraint.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}