package com.ecaservice.core.form.template.annotation;

import com.ecaservice.core.form.template.config.CoreFormTemplatesConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotations that enables form templates management.
 *
 * @author Roman Batygin
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(CoreFormTemplatesConfiguration.class)
public @interface EnableFormTemplates {
}
