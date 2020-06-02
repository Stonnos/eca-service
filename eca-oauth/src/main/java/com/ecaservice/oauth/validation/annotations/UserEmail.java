package com.ecaservice.oauth.validation.annotations;

import com.ecaservice.oauth.validation.UserEmailValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The annotated element must be existing user email.
 *
 * @author Roman Batygin
 */
@Documented
@Constraint(validatedBy = UserEmailValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface UserEmail {

    String message() default "{user.email.exists.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}