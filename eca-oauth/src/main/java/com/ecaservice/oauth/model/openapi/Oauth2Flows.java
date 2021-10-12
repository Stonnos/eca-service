package com.ecaservice.oauth.model.openapi;

import lombok.Data;

@Data
public class Oauth2Flows {
    private Oauth2Flow implicit;
    private Oauth2Flow password;
    private Oauth2Flow clientCredentials;
    private Oauth2Flow authorizationCode;
}
