package com.ecaservice.oauth.model;

import lombok.Data;

@Data
public class RequestBodyModel {

    private String contentType;

    private String required;

    private String bodyRef;

    private String example;
}
