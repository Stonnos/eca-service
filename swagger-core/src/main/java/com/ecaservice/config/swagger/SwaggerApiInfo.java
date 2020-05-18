package com.ecaservice.config.swagger;

import lombok.Data;

/**
 * Swagger api info.
 *
 * @author Roman Batygin
 */
@Data
public class SwaggerApiInfo {

    /**
     * Api title
     */
    private String title;

    /**
     * Api description
     */
    private String description;

    /**
     * Api author full name
     */
    private String author;

    /**
     * Api email
     */
    private String email;
}
