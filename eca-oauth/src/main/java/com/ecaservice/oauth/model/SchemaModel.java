package com.ecaservice.oauth.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SchemaModel {

    private String description;
    private String type;
    private String format;
    private BigDecimal maximum;
    private Boolean exclusiveMaximum;
    private BigDecimal minimum;
    private Boolean exclusiveMinimum;
    private Integer maxLength;
    private Integer minLength;
    private String pattern;
    private Integer maxItems;
    private Integer minItems;
}
