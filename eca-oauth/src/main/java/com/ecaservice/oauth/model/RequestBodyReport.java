package com.ecaservice.oauth.model;

import lombok.Data;

import java.util.List;

/**
 *
 * @author Roman Batygin
 */
@Data
public class RequestBodyReport {

    private String contentType;

    private String required;

    private String bodyRef;

    private String example;

    private List<FieldReport> schemaProperties;
}
