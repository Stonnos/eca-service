package com.ecaservice.oauth.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class MethodInfo {

    private String requestType;

    private String endpoint;

    private String summary;

    private String description;

    private RequestBodyModel requestBody;

    private List<RequestParameterModel> requestParameters;

    private List<ApiResponseModel> apiResponses;
}
