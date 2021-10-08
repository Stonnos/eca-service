package com.ecaservice.oauth.mapping;

import com.ecaservice.oauth.model.OpenApiModel;
import com.ecaservice.oauth.model.RequestBodyModel;
import com.ecaservice.oauth.model.RequestParameterModel;
import com.ecaservice.oauth.model.SchemaModel;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.parameters.RequestBody;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.Optional;

@Mapper
public interface OpenApiMapper {

    @Mapping(source = "info.title", target = "title")
    @Mapping(source = "info.description", target = "description")
    @Mapping(source = "info.contact.name", target = "author")
    @Mapping(source = "info.contact.email", target = "email")
    @Mapping(target = "components", ignore = true)
    OpenApiModel map(OpenAPI openAPI);

    @Mapping(source = "required", target = "required", defaultValue = "false")
    RequestBodyModel map(RequestBody requestBody);

    @Mapping(source = "required", target = "required", defaultValue = "false")
    @Mapping(source = "schema", target = "schemaModel")
    @Mapping(source = "example", target = "example", qualifiedByName = "objectToString")
    RequestParameterModel map(Parameter parameter);

    @Mapping(source = "pattern", target = "pattern", qualifiedByName = "mapPattern")
    @Mapping(source = "enum", target = "enumValues")
    SchemaModel map(Schema schema);

    List<RequestParameterModel> map(List<Parameter> parameters);

    @Named("objectToString")
    default String objectToString(Object object) {
        return Optional.ofNullable(object)
                .map(String::valueOf)
                .orElse(null);
    }

    @Named("mapPattern")
    default String mapPattern(String pattern) {
        return Optional.ofNullable(pattern)
                .map(val -> val.replace("|", "\\|"))
                .orElse(null);
    }
}
