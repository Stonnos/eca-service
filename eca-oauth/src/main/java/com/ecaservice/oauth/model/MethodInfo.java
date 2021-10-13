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
public class MethodInfo {

    private String requestType;

    private String endpoint;

    private String summary;

    private String description;

    private RequestBodyReport requestBody;

    private List<RequestParameterReport> requestParameters;

    private List<ApiResponseReport> apiResponses;

    private List<SecurityRequirementReport> security;
}
