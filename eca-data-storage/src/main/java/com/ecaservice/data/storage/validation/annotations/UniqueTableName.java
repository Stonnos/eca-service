package com.ecaservice.data.storage.validation.annotations;

import com.ecaservice.data.storage.validation.UniqueTableNameValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The annotated element must be unique table name.
 *
 * @author Roman Batygin
 */
@Documented
@Constraint(validatedBy = UniqueTableNameValidator.class)
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueTableName {

    String message() default "{tableName.unique.constraint.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}