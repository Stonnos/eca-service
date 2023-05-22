package com.ecaservice.data.storage.controller.internal;

import com.ecaservice.common.web.dto.ValidationErrorDto;
import com.ecaservice.data.storage.report.InstancesReportService;
import com.ecaservice.data.storage.service.StorageService;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import static com.ecaservice.config.swagger.OpenApi30Configuration.ECA_AUTHENTICATION_SECURITY_SCHEME;
import static com.ecaservice.config.swagger.OpenApi30Configuration.SCOPE_WEB;
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

    private final StorageService storageService;
    private final InstancesReportService instancesReportService;

    /**
     * Downloads instances report with selected attributes in json format.
     *
     * @param uuid                - instances uuid
     * @param httpServletResponse - http servlet response
     * @throws Exception in case of error
     */
    @Operation(
            description = "Downloads instances report with selected attributes in json format",
            summary = "Downloads instances report with selected attributes in json format",
            security = @SecurityRequirement(name = ECA_AUTHENTICATION_SECURITY_SCHEME, scopes = SCOPE_WEB),
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200"),
                    @ApiResponse(description = "Bad request", responseCode = "400",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "SelectedAttributesOutOfBoundsErrorCodeResponse",
                                                    ref = "#/components/examples/SelectedAttributesOutOfBoundsErrorCodeResponse"
                                            ),
                                    },
                                    array = @ArraySchema(schema = @Schema(implementation = ValidationErrorDto.class))
                            )
                    )
            }
    )
    @GetMapping(value = "/download_report")
    public void downloadInstancesReportWithSelectedAttributes(
            @Parameter(description = "Instances uuid", example = "1d2de514-3a87-4620-9b97-c260e24340de",
                    required = true)
            @RequestParam @Min(VALUE_1) @Max(Long.MAX_VALUE) String uuid,
            HttpServletResponse httpServletResponse) throws Exception {
        log.info("Request to download instances [{}] report", uuid);
        var instancesEntity = storageService.getByUuid(uuid);
        instancesReportService.generateJsonInstancesReportWithSelectedAttributes(instancesEntity, httpServletResponse);
    }
}
