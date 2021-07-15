package com.ecaservice.mail.config.swagger;

import com.ecaservice.config.swagger.OpenApi30Configuration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Swagger configuration for eca mail.
 *
 * @author Roman Batygin
 */
@Configuration
@Import(OpenApi30Configuration.class)
public class EcaMailSwagger2Configuration {
}
