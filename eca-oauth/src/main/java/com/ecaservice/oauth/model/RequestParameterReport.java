package com.ecaservice.oauth.model;

import lombok.Data;

/**
 *
 * @author Roman Batygin
 */
@Data
public class RequestParameterReport {

    private String name;

    private String description;

    private boolean required;

    private String in;

    private String example;

    private SchemaReport schema;
}
