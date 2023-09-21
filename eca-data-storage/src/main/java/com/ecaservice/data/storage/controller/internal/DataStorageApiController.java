package com.ecaservice.data.storage.controller.internal;

import com.ecaservice.common.error.model.ValidationErrorDto;
import com.ecaservice.data.storage.dto.ExportInstancesResponseDto;
import com.ecaservice.data.storage.service.ExportInstancesObjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import static com.ecaservice.config.swagger.OpenApi30Configuration.ECA_AUTHENTICATION_SECURITY_SCHEME;
import static com.ecaservice.config.swagger.OpenApi30Configuration.SCOPE_WEB;
import static com.ecaservice.web.dto.util.FieldConstraints.UUID_MAX_SIZE;
import static com.ecaservice.web.dto.util.FieldConstraints.UUID_PATTERN;
import static com.ecaservice.web.dto.util.FieldConstraints.VALUE_1;

/**
 * Data storage internal API.
 *
 * @author Roman Batygin
 */
@Validated
@Slf4j
@Tag(name = "Data storage internal API")
@RestController
@RequestMapping("/api/internal/instances")
@RequiredArgsConstructor
public class DataStorageApiController {

    private final ExportInstancesObjectService exportInstancesObjectService;

    /**
     * Exports valid instances with selected attributes and assigned class attribute to central data storage.
     *
     * @param uuid - instances uuid
     */
    @Operation(
            description = "Exports valid instances with selected attributes and assigned class attribute to central data storage",
            summary = "Exports valid instances with selected attributes and assigned class attribute to central data storage",
            security = @SecurityRequirement(name = ECA_AUTHENTICATION_SECURITY_SCHEME, scopes = SCOPE_WEB),
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "ExportValidInstancesResponseDto",
                                                    ref = "#/components/examples/ExportValidInstancesResponseDto"
                                            ),
                                    },
                                    schema = @Schema(implementation = ExportInstancesResponseDto.class)
                            )),
                    @ApiResponse(description = "Bad request", responseCode = "400",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "SelectedAttributesValuesIsTooLowErrorCodeResponse",
                                                    ref = "#/components/examples/SelectedAttributesValuesIsTooLowErrorCodeResponse"
                                            ),
                                    },
                                    array = @ArraySchema(schema = @Schema(implementation = ValidationErrorDto.class))
                            )
                    )
            }
    )
    @PostMapping(value = "/export-valid-instances")
    public ExportInstancesResponseDto exportValidInstances(
            @Parameter(description = "Instances uuid", example = "1d2de514-3a87-4620-9b97-c260e24340de",
                    required = true)
            @RequestParam @Pattern(regexp = UUID_PATTERN)
            @Size(min = VALUE_1, max = UUID_MAX_SIZE) String uuid) {
        log.info("Request to export valid instances report with uuid [{}]", uuid);
        return exportInstancesObjectService.exportValidInstances(uuid);
    }
}
