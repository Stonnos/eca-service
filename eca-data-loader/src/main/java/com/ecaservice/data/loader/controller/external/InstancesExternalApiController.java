package com.ecaservice.data.loader.controller.external;

import com.ecaservice.common.error.model.ValidationErrorDto;
import com.ecaservice.data.loader.dto.UploadInstancesResponseDto;
import com.ecaservice.data.loader.service.UploadInstancesService;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * Instances external API controller.
 *
 * @author Roman Batygin
 */
@Slf4j
@Tag(name = "Operations for external API")
@RestController
@RequestMapping("/api/external")
@RequiredArgsConstructor
public class InstancesExternalApiController {

    private final UploadInstancesService uploadInstancesService;

    /**
     * Uploads train data file to storage.
     *
     * @param instancesFile - instances file
     * @return upload response dto
     */
    @Operation(
            description = "Uploads train data file to storage",
            summary = "Uploads train data file to storage",
            // security = @SecurityRequirement(name = ECA_AUTHENTICATION_SECURITY_SCHEME, scopes = SCOPE_EXTERNAL_API),
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "UploadInstancesResponseDto",
                                                    ref = "#/components/examples/UploadInstancesResponseDto"
                                            ),
                                    },
                                    schema = @Schema(implementation = UploadInstancesResponseDto.class)
                            )
                    ),
                    @ApiResponse(description = "Not authorized", responseCode = "401",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "NotAuthorizedResponse",
                                                    ref = "#/components/examples/NotAuthorizedResponse"
                                            ),
                                    }
                            )
                    ),
                    @ApiResponse(description = "Bad request", responseCode = "400",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "UploadTrainDataBadRequestResponse",
                                                    ref = "#/components/examples/UploadTrainDataBadRequestResponse"
                                            ),
                                    },
                                    array = @ArraySchema(schema = @Schema(implementation = ValidationErrorDto.class))
                            )
                    )
            }
    )
    @PostMapping(value = "/upload-train-data", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public UploadInstancesResponseDto uploadInstances(
            @Parameter(description = "Training data file", required = true)
            @RequestParam MultipartFile instancesFile) {
        log.info("Request to upload train data file [{}]", instancesFile.getOriginalFilename());
        return uploadInstancesService.uploadInstances(instancesFile);
    }
}
