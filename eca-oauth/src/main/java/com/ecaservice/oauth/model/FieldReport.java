package com.ecaservice.oauth.model;

import lombok.Builder;
import lombok.Data;

/**
 *
 * @author Roman Batygin
 */
@Data
@Builder
public class FieldReport {
    private String fieldName;

    private String description;

    private boolean required;

    private SchemaReport schema;
}
