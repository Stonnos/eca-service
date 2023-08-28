package com.ecaservice.data.loader.controller.internal;

import com.ecaservice.common.error.model.ValidationErrorDto;
import com.ecaservice.data.loader.dto.InstancesMetaInfoDto;
import com.ecaservice.data.loader.service.InstancesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Instances internal API controller.
 *
 * @author Roman Batygin
 */
@Slf4j
@Tag(name = "Operations for internal API")
@RestController
@RequestMapping("/api/internal/instances")
@RequiredArgsConstructor
public class InstancesInternalApiController {

    private final InstancesService instancesService;

    /**
     * Gets instances meta info.
     *
     * @param uuid - instances uuid
     * @return instances meta info dto
     */
    @Operation(
            description = "Gets instances meta info",
            summary = "Gets instances meta info",
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "GetInstancesMetaInfoResponseDto",
                                                    ref = "#/components/examples/GetInstancesMetaInfoResponseDto"
                                            ),
                                    },
                                    schema = @Schema(implementation = InstancesMetaInfoDto.class)
                            )
                    ),
                    @ApiResponse(description = "Bad request", responseCode = "400",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "DataNotFoundResponse",
                                                    ref = "#/components/examples/DataNotFoundResponse"
                                            ),
                                    },
                                    array = @ArraySchema(schema = @Schema(implementation = ValidationErrorDto.class))
                            )
                    )
            }
    )
    @GetMapping(value = "/meta-info/{uuid}")
    public InstancesMetaInfoDto getInstancesMetaInfo(
            @Parameter(description = "Instances uuid", required = true) @PathVariable String uuid) {
        log.debug("Request to get instances [{}] meta info", uuid);
        return instancesService.getMetaInfo(uuid);
    }
}
