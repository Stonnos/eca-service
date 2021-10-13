package com.ecaservice.oauth.model;

import lombok.Data;

import java.util.List;

@Data
public class SecuritySchemaModel {

    private String type;
    private String description;
    private String name;
    private String ref;
    private String in;
    private String scheme;
    private String bearerFormat;
    private List<Oauth2FlowsReport> oauth2Flows;
}
