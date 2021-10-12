package com.ecaservice.oauth.model.openapi;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class Operation {
    private List<String> tags;
    private String summary;
    private String description;
    private String operationId;
    private List<Parameter> parameters;
    private RequestBody requestBody;
    private Map<String, ApiResponse> responses;
    private Boolean deprecated;
}
