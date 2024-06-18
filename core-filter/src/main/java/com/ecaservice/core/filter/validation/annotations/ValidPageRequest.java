package com.ecaservice.core.filter.validation.annotations;

import com.ecaservice.core.filter.validation.PageRequestValidator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The annotated element must be valid page request.
 *
 * @author Roman Batygin
 */
@Documented
@Constraint(validatedBy = PageRequestValidator.class)
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPageRequest {

    /**
     * Filter template name
     *
     * @return template name
     */
    String filterTemplateName() default "";

    /**
     * Error message text
     *
     * @return error message text
     */
    String message() default "";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
