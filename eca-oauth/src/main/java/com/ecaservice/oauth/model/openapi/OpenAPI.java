package com.ecaservice.oauth.model.openapi;

import lombok.Data;

import java.util.Map;

@Data
public class OpenAPI {

    private String openapi = "3.0.1";
    private Info info;
    private Map<String, PathItem> paths;
    private Components components;
}
