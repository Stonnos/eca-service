package com.ecaservice.oauth.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FieldModel {
    private String fieldName;

    private String description;

    private boolean required;

    private SchemaModel schemaModel;
}
