package com.ecaservice.oauth.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class SecurityRequirementReport {

    private String name;
    private List<String> scopes;
}
