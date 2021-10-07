package com.ecaservice.oauth.model;

import lombok.Data;

@Data
public class ApiResponseModel {

    private String responseCode;

    private String description;

    private String contentType;

    private String example;
}
