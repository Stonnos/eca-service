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
 * The annotated element must be valid user password.
 *
 * @author Roman Batygin
 */
@Documented
@Constraint(validatedBy = UserEmailValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface UserPassword {

    String message() default "{user.password.invalid.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
