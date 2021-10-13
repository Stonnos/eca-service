package com.ecaservice.oauth.model;

import lombok.Data;

@Data
public class RequestParameterModel {

    private String name;

    private String description;

    private boolean required;

    private String in;

    private String example;

    private SchemaModel schemaModel;
}
