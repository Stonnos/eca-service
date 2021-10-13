package com.ecaservice.oauth.model.openapi;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Roman Batygin
 */
@Data
public class Schema {
    private String name;
    private String title;
    private BigDecimal multipleOf;
    private BigDecimal maximum;
    private Boolean exclusiveMaximum;
    private BigDecimal minimum;
    private Boolean exclusiveMinimum;
    private Integer maxLength;
    private Integer minLength;
    private String pattern;
    private Integer maxItems;
    private Integer minItems;
    private Boolean uniqueItems;
    private Integer maxProperties;
    private Integer minProperties;
    private List<String> required;
    private String type;
    private Schema not;
    private Map<String, Schema> properties;
    private Map<String, String> additionalProperties;
    private String description;
    private String format;
    @JsonProperty("$ref")
    private String ref;
    private Boolean nullable;
    private Boolean readOnly;
    private Boolean writeOnly;
    private String example;
    private Boolean deprecated;
    @JsonProperty("_enum")
    private List<String> enums;
    private Schema items;
}
