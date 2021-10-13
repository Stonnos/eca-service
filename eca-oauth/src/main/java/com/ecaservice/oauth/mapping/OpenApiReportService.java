package com.ecaservice.oauth.mapping;

import com.ecaservice.oauth.model.ApiResponseReport;
import com.ecaservice.oauth.model.ComponentReport;
import com.ecaservice.oauth.model.FieldReport;
import com.ecaservice.oauth.model.MethodInfo;
import com.ecaservice.oauth.model.OpenApiReport;
import com.ecaservice.oauth.model.OperationReport;
import com.ecaservice.oauth.model.RequestBodyReport;
import com.ecaservice.oauth.model.SchemaReport;
import com.ecaservice.oauth.model.SecurityRequirementReport;
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

/**
 * Open api report service.
 *
 * @author Roman batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OpenApiReportService {

    private static final String SLASH_SEPARATOR = "/";
    private static final String PASSWORD_GRANT = "password";
    private static final String IMPLICIT_GRANT = "implicit";
    private static final String AUTHORIZATION_CODE_GRANT = "authorization_code";
    private static final String CLIENT_CREDENTIALS_GRANT = "client_credentials";

    private final OpenApiMapper openApiMapper;
    private final ObjectMapper exampleObjectMapper;

    /**
     * Builds open api report model.
     *
     * @param openAPI - open api schema
     * @return open api report model
     */
    public OpenApiReport buildReport(OpenAPI openAPI) {
        log.info("Starting to build open api report");
        OpenApiReport openApiReport = openApiMapper.map(openAPI);
        var paths = Optional.ofNullable(openAPI.getPaths()).orElse(Collections.emptyMap());
        var methods = paths.entrySet()
                .stream()
                .map(this::convertToMethodInfo).collect(Collectors.toList());
        var components = convertComponents(openAPI);
        var securitySchemes = buildSecuritySchemes(openAPI);
        openApiReport.setMethods(methods);
        openApiReport.setComponents(components);
        openApiReport.setSecuritySchemes(securitySchemes);
        log.info("Open api report has been built");
        return openApiReport;
    }

    private List<ComponentReport> convertComponents(OpenAPI openAPI) {
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
    private ComponentReport convertToComponentModel(String name, Schema schema) {
        Map<String, Schema> properties = Optional.ofNullable(schema.getProperties()).orElse(Collections.emptyMap());
        var requiredFields = Optional.ofNullable(schema.getRequired()).orElse(Collections.emptyList());
        List<FieldReport> fields = properties.entrySet()
                .stream()
                .map(entry -> convertToFieldModel(entry.getKey(),
                        requiredFields.contains(entry.getKey()), entry.getValue()))
                .collect(Collectors.toList());
        return ComponentReport.builder()
                .name(name)
                .description(schema.getDescription())
                .fields(fields)
                .build();
    }

    private FieldReport convertToFieldModel(String fieldName, boolean required, Schema schema) {
        SchemaReport schemaReport = openApiMapper.map(schema);
        return FieldReport.builder()
                .fieldName(fieldName)
                .description(schemaReport.getDescription())
                .required(required)
                .schema(schemaReport)
                .build();
    }

    private MethodInfo convertToMethodInfo(Map.Entry<String, PathItem> entry) {
        var operationModel = getOperationModel(entry.getValue());
        //TODO fix npe
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

    private List<SecurityRequirementReport> convertToSecurityRequirementModel(Operation operation) {
        if (CollectionUtils.isEmpty(operation.getSecurity())) {
            return Collections.emptyList();
        }
        List<SecurityRequirementReport> securityRequirementReports = newArrayList();
        operation.getSecurity().forEach(map -> map.forEach((name, scopes) -> {
            var securityRequirementModel = SecurityRequirementReport.builder()
                    .name(name)
                    .scopes(new ArrayList<>(scopes))
                    .build();
            securityRequirementReports.add(securityRequirementModel);
        }));
        return securityRequirementReports;
    }

    private OperationReport getOperationModel(PathItem pathItem) {
        return ObjectUtils.firstNonNull(
                getOperationOrNull(pathItem, PathItem::getGet, RequestMethod.GET),
                getOperationOrNull(pathItem, PathItem::getPost, RequestMethod.POST),
                getOperationOrNull(pathItem, PathItem::getPut, RequestMethod.PUT),
                getOperationOrNull(pathItem, PathItem::getDelete, RequestMethod.DELETE)
        );
    }

    private RequestBodyReport convertRequestBody(Operation operation) {
        return Optional.ofNullable(operation.getRequestBody())
                .map(requestBody -> {
                    RequestBodyReport requestBodyReport = openApiMapper.map(requestBody);
                    if (!CollectionUtils.isEmpty(requestBody.getContent())) {
                        var mediaType = requestBody.getContent().entrySet().iterator().next();
                        requestBodyReport.setContentType(mediaType.getKey());
                        requestBodyReport.setBodyRef(getBodyRef(mediaType.getValue().getSchema()));
                        requestBodyReport.setExample(getExample(mediaType.getValue()));
                    }
                    return requestBodyReport;
                }).orElse(null);
    }

    private List<ApiResponseReport> convertApiResponses(Operation operation) {
        if (CollectionUtils.isEmpty(operation.getResponses())) {
            return Collections.emptyList();
        }
        return operation.getResponses().entrySet()
                .stream()
                .map(entry -> convertApiResponse(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    private ApiResponseReport convertApiResponse(String responseCode, ApiResponse apiResponse) {
        ApiResponseReport apiResponseReport = new ApiResponseReport();
        apiResponseReport.setResponseCode(responseCode);
        apiResponseReport.setDescription(apiResponse.getDescription());
        if (!CollectionUtils.isEmpty(apiResponse.getContent())) {
            var mediaType = apiResponse.getContent().entrySet().iterator().next();
            apiResponseReport.setContentType(mediaType.getKey());
            apiResponseReport.setExample(getExample(mediaType.getValue()));
            apiResponseReport.setObjectTypeRef(getBodyRef(mediaType.getValue().getSchema()));
        }
        return apiResponseReport;
    }

    private OperationReport getOperationOrNull(PathItem pathItem,
                                               Function<PathItem, Operation> operationFunction,
                                               RequestMethod requestMethod) {
        return Optional.ofNullable(operationFunction.apply(pathItem))
                .map(operation -> OperationReport
                        .builder()
                        .operation(operation)
                        .requestMethod(requestMethod)
                        .build()
                ).orElse(null);
    }

    private String getBodyRef(Schema schema) {
        return Optional.ofNullable(schema)
                .map(Schema::getRef)
                .map(ref -> StringUtils.substringAfterLast(ref, SLASH_SEPARATOR))
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
            return Collections.emptyList();
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
            addOauth2Flow(flows.getPassword(), securitySchemaModel, PASSWORD_GRANT);
            addOauth2Flow(flows.getImplicit(), securitySchemaModel, IMPLICIT_GRANT);
            addOauth2Flow(flows.getAuthorizationCode(), securitySchemaModel, AUTHORIZATION_CODE_GRANT);
            addOauth2Flow(flows.getClientCredentials(), securitySchemaModel, CLIENT_CREDENTIALS_GRANT);
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
