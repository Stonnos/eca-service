package com.ecaservice.oauth.model;

import lombok.Data;

import java.util.List;

@Data
public class Oauth2FlowsModel {

    private String grantType;
    private String authorizationUrl;
    private String tokenUrl;
    private String refreshUrl;
    private List<String> scopes;
}
