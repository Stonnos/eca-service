package com.ecaservice.oauth.model.openapi;

import lombok.Data;

import java.util.Map;

@Data
public class MediaType {

    private Schema schema;
    private Map<String, Example> examples;
    private Object example;
}
