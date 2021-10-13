package com.ecaservice.oauth.model;

import lombok.Data;

import java.util.List;

/**
 *
 * @author Roman Batygin
 */
@Data
public class Oauth2FlowsReport {

    private String grantType;
    private String authorizationUrl;
    private String tokenUrl;
    private String refreshUrl;
    private List<String> scopes;
}
