package com.ecaservice.config.swagger;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.GrantType;

import java.util.List;

/**
 * Swagger security scheme options model.
 *
 * @author Roman Batygin
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SecuritySchemeOptions {

    /**
     * Security scheme name
     */
    private String name;

    /**
     * Grant type
     */
    private List<GrantType> grantTypes;

    /**
     * Authorization scopes
     */
    private List<AuthorizationScope> scopes;
}
