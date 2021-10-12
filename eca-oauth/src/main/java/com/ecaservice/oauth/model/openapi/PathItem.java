package com.ecaservice.oauth.model.openapi;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class PathItem {
    private String summary;
    private String description;
    private Operation get;
    private Operation put;
    private Operation post;
    private Operation delete;
    private Operation options;
    private Operation head;
    private Operation patch;
    private Operation trace;
    private List<Parameter> parameters;
    @JsonProperty("$ref")
    private String ref;
}
