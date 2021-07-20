package com.ecaservice.config.swagger;

import lombok.Data;

import java.util.List;

/**
 * Open api auth properties.
 *
 * @author Roman Batygin
 */
@Data
public class OpenApiAuthProperties {

    /**
     * Scopes list
     */
    private List<String> scopes;
}
