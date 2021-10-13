package com.ecaservice.oauth.model;

import lombok.Data;

import java.util.List;

/**
 *
 * @author Roman Batygin
 */
@Data
public class OpenApiReport {

    private String title;

    private String description;

    private String author;

    private String email;

    private List<MethodInfo> methods;

    private List<ComponentReport> components;

    private List<SecuritySchemaModel> securitySchemes;
}
