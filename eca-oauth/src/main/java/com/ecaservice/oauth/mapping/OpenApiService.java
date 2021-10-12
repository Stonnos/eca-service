package com.ecaservice.oauth.mapping;

import com.ecaservice.oauth.model.ApiResponseModel;
import com.ecaservice.oauth.model.ComponentModel;
import com.ecaservice.oauth.model.FieldModel;
import com.ecaservice.oauth.model.MethodInfo;
import com.ecaservice.oauth.model.OpenApiModel;
import com.ecaservice.oauth.model.OperationModel;
import com.ecaservice.oauth.model.RequestBodyModel;
import com.ecaservice.oauth.model.SchemaModel;
import com.ecaservice.oauth.model.SecurityRequirementModel;
import com.ecaservice.oauth.model.SecuritySchemaModel;
import com.ecaservice.oauth.model.openapi.ApiResponse;
import com.ecaservice.oauth.model.openapi.Components;
import com.ecaservice.oauth.model.openapi.MediaType;
import com.ecaservice.oauth.model.openapi.Oauth2Flow;
import com.ecaservice.oauth.model.openapi.OpenAPI;
import com.ecaservice.oauth.model.openapi.Operation;
import com.ecaservice.oauth.model.openapi.PathItem;
import com.ecaservice.oauth.model.openapi.Schema;
import com.ecaservice.oauth.model.openapi.SecurityScheme;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;

@Slf4j
@Service
@RequiredArgsConstructor
public class OpenApiService {

    private final OpenApiMapper openApiMapper;
    private final ObjectMapper exampleObjectMapper;

    public OpenApiModel process(OpenAPI openAPI) {
        OpenApiModel openApiModel = openApiMapper.map(openAPI);
        var methods = openAPI.getPaths().entrySet()
                .stream()
                .map(this::convertToMethodInfo).collect(Collectors.toList());
        var components = convertComponents(openAPI);
        var securitySchemes = buildSecuritySchemes(openAPI);
        openApiModel.setMethods(methods);
        openApiModel.setComponents(components);
        openApiModel.setSecuritySchemes(securitySchemes);
        return openApiModel;
    }

    private List<ComponentModel> convertComponents(OpenAPI openAPI) {
        if (Optional.ofNullable(openAPI.getComponents()).isEmpty() ||
                CollectionUtils.isEmpty(openAPI.getComponents().getSchemas())) {
            return Collections.emptyList();
        }
        return openAPI.getComponents().getSchemas()
                .entrySet()
                .stream()
                .map(entry -> convertToComponentModel(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    private ComponentModel convertToComponentModel(String name, Schema schema) {
        Map<String, Schema> properties = Optional.ofNullable(schema.getProperties()).orElse(Collections.emptyMap());
        var requiredFields = Optional.ofNullable(schema.getRequired()).orElse(Collections.emptyList());
        List<FieldModel> fields = properties.entrySet()
                .stream()
                .map(entry -> convertToFieldModel(entry.getKey(),
                        requiredFields.contains(entry.getKey()), entry.getValue()))
                .collect(Collectors.toList());
        return ComponentModel.builder()
                .name(name)
                .description(schema.getDescription())
                .fields(fields)
                .build();
    }

    private FieldModel convertToFieldModel(String fieldName, boolean required, Schema schema) {
        SchemaModel schemaModel = openApiMapper.map(schema);
        return FieldModel.builder()
                .fieldName(fieldName)
                .description(schemaModel.getDescription())
                .required(Boolean.toString(required))
                .schemaModel(schemaModel)
                .build();
    }

    private MethodInfo convertToMethodInfo(Map.Entry<String, PathItem> entry) {
        var operationModel = getOperationModel(entry.getValue());
        var operation = operationModel.getOperation();
        var requestParameters = openApiMapper.map(operation.getParameters());
        var apiResponses = convertApiResponses(operation);
        var requestBodyModel = convertRequestBody(operation);
        var securityRequirementModel = convertToSecurityRequirementModel(operation);
        return MethodInfo.builder()
                .requestType(operationModel.getRequestMethod().name())
                .endpoint(entry.getKey())
                .summary(operation.getSummary())
                .description(operation.getDescription())
                .requestBody(requestBodyModel)
                .requestParameters(requestParameters)
                .apiResponses(apiResponses)
                .security(securityRequirementModel)
                .build();
    }

    private List<SecurityRequirementModel> convertToSecurityRequirementModel(Operation operation) {
        if (CollectionUtils.isEmpty(operation.getSecurity())) {
            return Collections.emptyList();
        }
        List<SecurityRequirementModel> securityRequirementModels = newArrayList();
        operation.getSecurity().forEach(map -> map.forEach((name, scopes) -> {
            var securityRequirementModel = SecurityRequirementModel.builder()
                    .name(name)
                    .scopes(new ArrayList<>(scopes))
                    .build();
            securityRequirementModels.add(securityRequirementModel);
        }));
        return securityRequirementModels;
    }

    private OperationModel getOperationModel(PathItem pathItem) {
        return ObjectUtils.firstNonNull(
                getOperationOrNull(pathItem, PathItem::getGet, RequestMethod.GET),
                getOperationOrNull(pathItem, PathItem::getPost, RequestMethod.POST),
                getOperationOrNull(pathItem, PathItem::getPut, RequestMethod.PUT),
                getOperationOrNull(pathItem, PathItem::getDelete, RequestMethod.DELETE)
        );
    }

    private RequestBodyModel convertRequestBody(Operation operation) {
        return Optional.ofNullable(operation.getRequestBody())
                .map(requestBody -> {
                    RequestBodyModel requestBodyModel = openApiMapper.map(requestBody);
                    var mediaType = requestBody.getContent().entrySet().iterator().next();
                    requestBodyModel.setContentType(mediaType.getKey());
                    requestBodyModel.setBodyRef(getBodyRef(mediaType.getValue().getSchema()));
                    requestBodyModel.setExample(getExample(mediaType.getValue()));
                    return requestBodyModel;
                }).orElse(null);
    }

    private List<ApiResponseModel> convertApiResponses(Operation operation) {
        if (CollectionUtils.isEmpty(operation.getResponses())) {
            return Collections.emptyList();
        }
        return operation.getResponses().entrySet()
                .stream()
                .map(entry -> convertApiResponse(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    private ApiResponseModel convertApiResponse(String responseCode, ApiResponse apiResponse) {
        ApiResponseModel apiResponseModel = new ApiResponseModel();
        apiResponseModel.setResponseCode(responseCode);
        apiResponseModel.setDescription(apiResponse.getDescription());
        if (!CollectionUtils.isEmpty(apiResponse.getContent())) {
            var mediaType = apiResponse.getContent().entrySet().iterator().next();
            apiResponseModel.setContentType(mediaType.getKey());
            apiResponseModel.setExample(getExample(mediaType.getValue()));
            apiResponseModel.setObjectTypeRef(getBodyRef(mediaType.getValue().getSchema()));
        }
        return apiResponseModel;
    }

    private OperationModel getOperationOrNull(PathItem pathItem,
                                              Function<PathItem, Operation> operationFunction,
                                              RequestMethod requestMethod) {
        return Optional.ofNullable(operationFunction.apply(pathItem))
                .map(operation -> OperationModel
                        .builder()
                        .operation(operation)
                        .requestMethod(requestMethod)
                        .build()
                ).orElse(null);
    }

    private String getBodyRef(Schema schema) {
        return Optional.ofNullable(schema)
                .map(Schema::getRef)
                .map(ref -> StringUtils.substringAfterLast(ref, "/"))
                .orElse(null);
    }

    @SneakyThrows
    private String getExample(MediaType mediaType) {
        if (mediaType.getExample() == null) {
            return null;
        } else {
            return exampleObjectMapper.writeValueAsString(mediaType.getExample());
        }
    }

    private List<SecuritySchemaModel> buildSecuritySchemes(OpenAPI openAPI) {
        if (Optional.ofNullable(openAPI.getComponents()).map(Components::getSecuritySchemes).isEmpty()) {
            return null;
        }
        return openAPI.getComponents()
                .getSecuritySchemes()
                .values()
                .stream()
                .map(this::buildSecurityScheme)
                .collect(Collectors.toList());
    }

    private SecuritySchemaModel buildSecurityScheme(SecurityScheme securityScheme) {
        var securitySchemaModel = openApiMapper.map(securityScheme);
        securitySchemaModel.setOauth2Flows(newArrayList());
        var flows = securityScheme.getFlows();
        if (Optional.ofNullable(flows).isPresent()) {
            addOauth2Flow(flows.getPassword(), securitySchemaModel, "password");
            addOauth2Flow(flows.getImplicit(), securitySchemaModel, "implicit");
            addOauth2Flow(flows.getAuthorizationCode(), securitySchemaModel, "authorization_code");
            addOauth2Flow(flows.getClientCredentials(), securitySchemaModel, "client_credentials");
        }
        return securitySchemaModel;
    }

    private void addOauth2Flow(Oauth2Flow oauth2Flow, SecuritySchemaModel securitySchemaModel, String grantType) {
        Optional.ofNullable(oauth2Flow)
                .ifPresent(flow -> {
                    var flowModel = openApiMapper.map(flow);
                    flowModel.setGrantType(grantType);
                    securitySchemaModel.getOauth2Flows().add(flowModel);
                });
    }
}
