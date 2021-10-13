package com.ecaservice.oauth.model.openapi;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;

/**
 *
 * @author Roman Batygin
 */
@Data
public class ApiResponse {

    private String description;
    private Map<String, MediaType> content;
    @JsonProperty("$ref")
    private String $ref;
}
