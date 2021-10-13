package com.ecaservice.oauth.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 *
 * @author Roman Batygin
 */
@Data
@Builder
public class ComponentReport {

    private String name;

    private String description;

    private List<FieldReport> fields;
}
