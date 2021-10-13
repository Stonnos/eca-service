package com.ecaservice.oauth.mapping;

import com.ecaservice.oauth.model.Oauth2FlowsReport;
import com.ecaservice.oauth.model.OpenApiReport;
import com.ecaservice.oauth.model.RequestBodyReport;
import com.ecaservice.oauth.model.RequestParameterReport;
import com.ecaservice.oauth.model.SchemaReport;
import com.ecaservice.oauth.model.SecuritySchemaModel;
import com.ecaservice.oauth.model.openapi.Oauth2Flow;
import com.ecaservice.oauth.model.openapi.OpenAPI;
import com.ecaservice.oauth.model.openapi.Parameter;
import com.ecaservice.oauth.model.openapi.RequestBody;
import com.ecaservice.oauth.model.openapi.Schema;
import com.ecaservice.oauth.model.openapi.SecurityScheme;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Mapper
public interface OpenApiMapper {

    @Mapping(source = "info.title", target = "title")
    @Mapping(source = "info.description", target = "description")
    @Mapping(source = "info.contact.name", target = "author")
    @Mapping(source = "info.contact.email", target = "email")
    @Mapping(target = "methods", ignore = true)
    @Mapping(target = "components", ignore = true)
    @Mapping(target = "securitySchemes", ignore = true)
    OpenApiReport map(OpenAPI openAPI);

    @Mapping(source = "required", target = "required", defaultValue = "false")
    RequestBodyReport map(RequestBody requestBody);

    RequestParameterReport map(Parameter parameter);

    @Mapping(source = "pattern", target = "pattern", qualifiedByName = "mapPattern")
    @Mapping(source = "enums", target = "enumValues")
    @Mapping(source = "ref", target = "objectTypeRef", qualifiedByName = "mapRef")
    @Mapping(source = "items.ref", target = "itemsObjectRef", qualifiedByName = "mapRef")
    SchemaReport map(Schema schema);

    @Mapping(target = "oauth2Flows", ignore = true)
    SecuritySchemaModel map(SecurityScheme securityScheme);

    @Mapping(target = "scopes", ignore = true)
    Oauth2FlowsReport map(Oauth2Flow oauth2Flow);

    List<RequestParameterReport> map(List<Parameter> parameters);

    @AfterMapping
    default void mapScopes(Oauth2Flow oauth2Flow, @MappingTarget Oauth2FlowsReport oauth2FlowsReport) {
        if (!CollectionUtils.isEmpty(oauth2Flow.getScopes())) {
            oauth2FlowsReport.setScopes(new ArrayList<>(oauth2Flow.getScopes().keySet()));
        }
    }

    @Named("mapPattern")
    default String mapPattern(String pattern) {
        return Optional.ofNullable(pattern)
                .map(val -> val.replace("|", "\\|"))
                .orElse(null);
    }

    @Named("mapRef")
    default String mapRef(String value) {
        return Optional.ofNullable(value)
                .map(ref -> StringUtils.substringAfterLast(ref, "/"))
                .orElse(null);
    }
}
