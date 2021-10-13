package com.ecaservice.oauth.model.openapi;


import lombok.Data;

import java.util.Map;

/**
 *
 * @author Roman Batygin
 */
@Data
public class Components {

    private Map<String, Schema> schemas;
    private Map<String, ApiResponse> responses;
    private Map<String, Parameter> parameters;
    private Map<String, Example> examples;
    private Map<String, RequestBody> requestBodies;
    private Map<String, SecurityScheme> securitySchemes;
}
