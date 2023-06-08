package com.ecaservice.server.controller.web;

import com.ecaservice.common.error.model.ValidationErrorDto;
import com.ecaservice.server.service.ers.ClassifierOptionsRequestService;
import com.ecaservice.web.dto.model.ClassifierOptionsRequestDto;
import com.ecaservice.web.dto.model.ClassifierOptionsRequestsPageDto;
import com.ecaservice.web.dto.model.PageDto;
import com.ecaservice.web.dto.model.PageRequestDto;
import io.swagger.v3.oas.annotations.Operation;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static com.ecaservice.config.swagger.OpenApi30Configuration.ECA_AUTHENTICATION_SECURITY_SCHEME;
import static com.ecaservice.config.swagger.OpenApi30Configuration.SCOPE_WEB;

/**
 * Classifier options requests API for web application.
 *
 * @author Roman Batygin
 */
@Tag(name = "Classifier options requests API for web application")
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
public class ClassifierOptionsRequestController {

    private final ClassifierOptionsRequestService classifierOptionsRequestService;

    /**
     * Finds classifiers options requests models with specified options such as filter, sorting and paging.
     *
     * @param pageRequestDto - page request dto
     * @return classifiers options requests models page
     */
    @PreAuthorize("#oauth2.hasScope('web')")
    @Operation(
            description = "Finds classifiers options requests models with specified options",
            summary = "Finds classifiers options requests models with specified options",
            security = @SecurityRequirement(name = ECA_AUTHENTICATION_SECURITY_SCHEME, scopes = SCOPE_WEB),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = {
                    @Content(examples = {
                            @ExampleObject(
                                    name = "OptimalClassifierOptionsPageRequest",
                                    ref = "#/components/examples/OptimalClassifierOptionsPageRequest"
                            )
                    })
            }),
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "OptimalClassifierOptionsPageResponse",
                                                    ref = "#/components/examples/OptimalClassifierOptionsPageResponse"
                                            )
                                    },
                                    schema = @Schema(implementation = ClassifierOptionsRequestsPageDto.class)
                            )
                    ),
                    @ApiResponse(description = "Not authorized", responseCode = "401",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "NotAuthorizedResponse",
                                                    ref = "#/components/examples/NotAuthorizedResponse"
                                            )
                                    }
                            )
                    ),
                    @ApiResponse(description = "Bad request", responseCode = "400",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "BadPageRequestResponse",
                                                    ref = "#/components/examples/BadPageRequestResponse"
                                            )
                                    },
                                    array = @ArraySchema(schema = @Schema(implementation = ValidationErrorDto.class))
                            )
                    )
            }
    )
    @PostMapping(value = "/classifiers-options-requests")
    public PageDto<ClassifierOptionsRequestDto> getClassifierOptionsRequestModels(
            @Valid @RequestBody PageRequestDto pageRequestDto) {
        log.info("Received classifiers options requests models page request: {}", pageRequestDto);
        return classifierOptionsRequestService.getClassifierOptionsRequestsPage(pageRequestDto);
    }
}
