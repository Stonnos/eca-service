package com.ecaservice.oauth.model.openapi;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Example {

    private String summary;
    private String description;
    private String value;
    private String externalValue;
    @JsonProperty("$ref")
    private String ref;
}
