package com.ecaservice.oauth.model;

import lombok.Data;

/**
 *
 * @author Roman Batygin
 */
@Data
public class ApiResponseReport {

    private String responseCode;

    private String description;

    private String contentType;

    private String example;

    private String objectTypeRef;
}
