package com.ecaservice.oauth.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ComponentModel {

    private String name;

    private String description;

    private List<FieldModel> fields;
}
