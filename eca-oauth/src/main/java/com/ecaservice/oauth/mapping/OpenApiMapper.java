package com.ecaservice.oauth.mapping;

import com.ecaservice.oauth.model.Oauth2FlowsModel;
import com.ecaservice.oauth.model.OpenApiModel;
import com.ecaservice.oauth.model.RequestBodyModel;
import com.ecaservice.oauth.model.RequestParameterModel;
import com.ecaservice.oauth.model.SchemaModel;
import com.ecaservice.oauth.model.SecuritySchemaModel;
import com.ecaservice.oauth.model.openapi.Oauth2Flow;
import com.ecaservice.oauth.model.openapi.OpenAPI;
import com.ecaservice.oauth.model.openapi.Parameter;
import com.ecaservice.oauth.model.openapi.RequestBody;
import com.ecaservice.oauth.model.openapi.Schema;
import com.ecaservice.oauth.model.openapi.SecurityScheme;
import io.swagger.v3.oas.models.security.OAuthFlow;
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
import java.util.stream.Collectors;

@Mapper
public interface OpenApiMapper {

    @Mapping(source = "info.title", target = "title")
    @Mapping(source = "info.description", target = "description")
    @Mapping(source = "info.contact.name", target = "author")
    @Mapping(source = "info.contact.email", target = "email")
    @Mapping(target = "methods", ignore = true)
    @Mapping(target = "components", ignore = true)
    @Mapping(target = "securitySchemes", ignore = true)
    OpenApiModel map(OpenAPI openAPI);

    @Mapping(source = "required", target = "required", defaultValue = "false")
    RequestBodyModel map(RequestBody requestBody);

    @Mapping(source = "required", target = "required", defaultValue = "false")
    @Mapping(source = "schema", target = "schemaModel")
    RequestParameterModel map(Parameter parameter);

    @Mapping(source = "pattern", target = "pattern", qualifiedByName = "mapPattern")
    @Mapping(source = "enums", target = "enumValues")
    @Mapping(source = "ref", target = "objectTypeRef", qualifiedByName = "mapRef")
    @Mapping(source = "items.ref", target = "itemsObjectRef", qualifiedByName = "mapRef")
    SchemaModel map(Schema schema);

    @Mapping(target = "oauth2Flows", ignore = true)
    SecuritySchemaModel map(SecurityScheme securityScheme);

    @Mapping(target = "scopes", ignore = true)
    Oauth2FlowsModel map(Oauth2Flow oauth2Flow);

    List<RequestParameterModel> map(List<Parameter> parameters);

    @AfterMapping
    default void mapScopes(Oauth2Flow oauth2Flow, @MappingTarget Oauth2FlowsModel oauth2FlowsModel) {
        if (!CollectionUtils.isEmpty(oauth2Flow.getScopes())) {
            oauth2FlowsModel.setScopes(new ArrayList<>(oauth2Flow.getScopes().keySet()));
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
