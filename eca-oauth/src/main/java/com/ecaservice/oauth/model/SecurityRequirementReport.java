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
public class SecurityRequirementReport {

    private String name;
    private List<String> scopes;
}
