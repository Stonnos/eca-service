package com.ecaservice.oauth.model;

import lombok.Data;

import java.util.List;

@Data
public class OpenApiModel {

    private String title;

    private String description;

    private String author;

    private String email;

    private List<MethodInfo> methods;

    private List<ComponentModel> components;
}
